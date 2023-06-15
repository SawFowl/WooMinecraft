/*
 * Woo Minecraft Donation plugin
 * Author:	   Jerry Wood
 * Author URI: http://plugish.com
 * License:	   GPLv2
 * 
 * Copyright 2014 All rights Reserved
 * 
 */
package com.plugish.woominecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.plugish.woominecraft.pojo.Order;
import com.plugish.woominecraft.pojo.WMCPojo;
import com.plugish.woominecraft.pojo.WMCProcessedOrders;

import net.kyori.adventure.text.Component;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.event.LocaleServiseEvent;
import sawfowl.woominecraft.Config;
import sawfowl.woominecraft.SpongeRunner;
import sawfowl.woominecraft.commands.Woo;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.event.lifecycle.StoppedGameEvent;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.reference.ConfigurationReference;
import org.spongepowered.configurate.reference.ValueReference;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

@Plugin("woominecraft")
public final class WooMinecraft {

	static WooMinecraft instance;
	private LocaleService localeService;
	private ConfigurationReference<CommentedConfigurationNode> configurationReference;
	private ValueReference<Config, CommentedConfigurationNode> config;
	private PluginContainer pluginContainer;
	private Logger logger;
	private Path configDir;
	private boolean onlineMode;
	private boolean proxy;
	private boolean enable = true;

	public Logger getLogger() {
		return logger;
	}

	public PluginContainer getContainer() {
		return pluginContainer;
	}

	public Locale getSystemOrDefaultLocale() {
		return localeService.getSystemOrDefaultLocale();
	}

	public String getLocalizedString(Locale locale, Object... path) {
		return localeService.getOrDefaultLocale("woominecraft", locale).getString(path);
	}

	public String getLocalizedLogString(Locale locale, Object path) {
		return localeService.getOrDefaultLocale("woominecraft", locale).getString("log", path);
	}

	public Component getLocalizedText(Locale locale, Object... path) {
		return localeService.getOrDefaultLocale("woominecraft", locale).getComponent(false, path);
	}

	public Component getLocalizedGeneralText(Locale locale, Object path) {
		return localeService.getOrDefaultLocale("woominecraft", locale).getComponent(false, "general", path);
	}

	public Config getConfig() {
		return config.get();
	}

	public boolean isEnable() {
		return enable;
	}

	/**
	 * Stores the player data to prevent double checks.
	 *
	 * i.e. name:uuid:true|false
	 */
	private List<String> PlayersMap = new ArrayList<>();

	@Inject
	public WooMinecraft(PluginContainer pluginContainer, @ConfigDir(sharedRoot = false) Path configDirectory) {
		instance = this;
		logger = LogManager.getLogger("WooMinecraft");
		this.pluginContainer = pluginContainer;
		configDir = configDirectory;
	}

	public static final String NL = System.getProperty("line.separator");

	@Listener
	public void onConstruct(LocaleServiseEvent.Construct event) {
		instance = this;
		localeService = event.getLocaleService();
	}

	@Listener
	public void onStarted(StartedEngineEvent<Server> event) throws ConfigurateException {
		if(!enable) return;
		onlineMode = Sponge.server().isOnlineModeEnabled();
		proxy = Sponge.configManager().pluginConfig(Sponge.pluginManager().plugin(PluginManager.SPONGE_PLUGIN_ID).get()).config().load().node("modules", "ip-forwarding").getBoolean();
		if (!onlineMode && !proxy) {
			getLogger().error("WooMinecraft doesn't support offline mode");
			enable = false;
			return;
		}
		configurationReference = YamlConfigurationLoader.builder().nodeStyle(NodeStyle.BLOCK).defaultOptions(localeService.getConfigurationOptions()).path(configDir.resolve("Config.yml")).build().loadToReference();
		this.config = configurationReference.referenceTo(Config.class);
		if(!configDir.resolve("Config.yml").toFile().exists()) configurationReference.save();
		localeService.saveAssetLocales("woominecraft");

		// Log when plugin is initialized.
		getLogger().info(getLocalizedLogString(getSystemOrDefaultLocale(), "com_init"));

		// Setup the scheduler
		Sponge.asyncScheduler().submit(Task.builder().plugin(pluginContainer).delay(config.get().getUpdateInterval(), TimeUnit.SECONDS).interval(config.get().getUpdateInterval(), TimeUnit.SECONDS).execute(new SpongeRunner(instance)).build());
		
		// Log when plugin is fully enabled ( setup complete ).
		getLogger().info(getLocalizedLogString(getSystemOrDefaultLocale(), "enabled"));
	}

	@Listener
	public void onCommandRegister(RegisterCommandEvent<Parameterized> event) throws ConfigurateException {
		event.register(pluginContainer, new Woo(instance).build(), "woo");
	}

	@Listener
	public void onDisable(StoppedGameEvent event) {
		getLogger().info(getLocalizedLogString(getSystemOrDefaultLocale(), "disabled"));
	}

	/**
	 * Validates the basics needed in the config.yml file.
	 *
	 * Multiple reports of user configs not having keys etc... so this will ensure they know of this
	 * and will not allow checks to continue if the required data isn't set in the config.
	 *
	 * @throws Exception Reason for failing to validate the config.
	 */
	private void validateConfig() throws Exception {

		if ( 1 > this.getConfig().getUrl().length() ) {
			throw new Exception(getLocalizedLogString(getSystemOrDefaultLocale(), "empty_url"));
		} else if ( this.getConfig().getUrl().equals( "http://playground.dev" ) ) {
			throw new Exception(getLocalizedLogString(getSystemOrDefaultLocale(), "default_url"));
		} else if ( 1 > this.getConfig().getKey().length() ) {
			throw new Exception(getLocalizedLogString(getSystemOrDefaultLocale(), "empty_key"));
		}
	}

	/**
	 * Gets the site URL
	 *
	 * @return URL
	 * @throws Exception Why the URL failed.
	 */
	public URL getSiteURL() throws Exception {
		// Switches for pretty or non-pretty permalink support for REST urls.
		boolean usePrettyPermalinks = this.getConfig().isPrettyPermalinks();
		String baseUrl = getConfig().getUrl() + "/wp-json/wmc/v1/server/";
		if ( ! usePrettyPermalinks ) {
			baseUrl = getConfig().getUrl() + "/index.php?rest_route=/wmc/v1/server/";

			String customRestUrl = this.getConfig().getRestBasePath();
			if ( ! customRestUrl.isEmpty() ) {
				baseUrl = customRestUrl;
			}
		}

		debug_log( getLocalizedLogString(getSystemOrDefaultLocale(), "check_base_url") + baseUrl );
		return new URL( baseUrl + getConfig().getKey() );
	}

	/**
	 * Checks all online players against the
	 * website's database looking for pending donation deliveries
	 *
	 * @return boolean
	 * @throws Exception Why the operation failed.
	 */
	public boolean check() throws Exception {

		// Make 100% sure the config has at least a key and url
		this.validateConfig();

		// Contact the server.
		String pendingOrders = getPendingOrders();
		debug_log( getLocalizedLogString(getSystemOrDefaultLocale(), "site_reply") + NL + pendingOrders.substring( 0, Math.min(pendingOrders.length(), 64) ) + "..." );

		// Server returned an empty response, bail here.
		if ( pendingOrders.isEmpty() ) {
			debug_log( getLocalizedLogString(getSystemOrDefaultLocale(), "empty_pending_orders"), 2 );
			return false;
		}

		// Create new object from JSON response.
		Gson gson = new GsonBuilder().create();
		WMCPojo wmcPojo = gson.fromJson( pendingOrders, WMCPojo.class );
		List<Order> orderList = wmcPojo.getOrders();

		// Validate we can indeed process what we need to.
		if ( wmcPojo.getData() != null ) {
			// We have an error, so we need to bail.
			wmc_log( getLocalizedLogString(getSystemOrDefaultLocale(), "code") + wmcPojo.getCode(), 3 );
			throw new Exception( wmcPojo.getMessage() );
		}

		if ( orderList == null || orderList.isEmpty() ) {
			wmc_log( getLocalizedLogString(getSystemOrDefaultLocale(), "no_orders"), 2 );
			return false;
		}

		// foreach ORDERS in JSON feed
		List<Integer> processedOrders = new ArrayList<>();
		for ( Order order : orderList ) {
			Optional<ServerPlayer> optPlayer = Sponge.server().player(order.getPlayer());
			if (!optPlayer.isPresent()) {
				debug_log( getLocalizedLogString(getSystemOrDefaultLocale(), "player_not_present"), 2 );
				continue;
			}
			ServerPlayer player = optPlayer.get();
			// World whitelisting.
			if ( getConfig().getWhitelistWorlds().isEnable() ) {
				List<String> whitelistWorlds = getConfig().getWhitelistWorlds().getList();
				String playerWorld = player.world().key().asString();
				if (!whitelistWorlds.contains(playerWorld)) {
					wmc_log( getLocalizedLogString(getSystemOrDefaultLocale(), "unauthorized_world").replace("%player%", player.name()).replace("%world%", playerWorld) );
					continue;
				}
			}

			// Walk over all commands and run them at the next available tick.
			for ( String command : order.getCommands() ) {
				//Auth player against Mojang api
				if ( ! isPaidUser( player ) ) {
					debug_log( getLocalizedLogString(getSystemOrDefaultLocale(), "not_paid_user ").replace("%user%", player.name()) );
					return false;
				}

				Sponge.server().scheduler().executor(pluginContainer).execute(() -> {
					Sponge.server().commandManager().complete(command);
				});
			}

			debug_log( getLocalizedLogString(getSystemOrDefaultLocale(), "add_item") + order.getOrderId() );
			processedOrders.add( order.getOrderId() );
			debug_log( getLocalizedLogString(getSystemOrDefaultLocale(), "processed_length") + processedOrders.size() );
		}

		// If it's empty, we skip it.
		if ( processedOrders.isEmpty() ) {
			return false;
		}

		// Send/update processed orders.
		return sendProcessedOrders( processedOrders );
	}

	/**
	 * Sends the processed orders to the site.
	 *
	 * @param processedOrders A list of order IDs which were processed.
	 * @return boolean
	 */
	private boolean sendProcessedOrders( List<Integer> processedOrders ) throws Exception {
		// Build the GSON data to send.
		Gson gson = new Gson();
		WMCProcessedOrders wmcProcessedOrders = new WMCProcessedOrders();
		wmcProcessedOrders.setProcessedOrders( processedOrders );
		String orders = gson.toJson( wmcProcessedOrders );

		// Setup the client.
		OkHttpClient client = new OkHttpClient();

		// Process stuffs now.
		RequestBody body = RequestBody.create( MediaType.parse( "application/json; charset=utf-8" ), orders );
		Request request = new Request.Builder().url( getSiteURL() ).post( body ).build();
		Response response = client.newCall( request ).execute();

		// If the body is empty we can do nothing.
		if ( null == response.body() ) {
			throw new Exception( getLocalizedLogString(getSystemOrDefaultLocale(), "empty_response") );
		}

		// Get the JSON reply from the endpoint.
		WMCPojo wmcPojo = gson.fromJson( response.body().string(), WMCPojo.class );
		if ( null != wmcPojo.getCode() ) {
			wmc_log( getLocalizedLogString(getSystemOrDefaultLocale(), "received_error_post") + wmcPojo.getCode(), 3 );
			throw new Exception( wmcPojo.getMessage() );
		}

		return true;
	}

	/**
	 * If debugging is enabled.
	 *
	 * @return boolean
	 */
	public boolean isDebug() {
		return getConfig().isDebug();
	}

	/**
	 * Gets pending orders from the WordPress JSON endpoint.
	 *
	 * @return String
	 * @throws Exception On failure.
	 */
	private String getPendingOrders() throws Exception {
		URL baseURL = getSiteURL();
		BufferedReader input = null;
		try {
			Reader streamReader = new InputStreamReader( baseURL.openStream() );
			input = new BufferedReader( streamReader );
		} catch (IOException e) { // FileNotFoundException extends IOException, so we just catch that here.
			String key = getConfig().getKey();
			String msg = e.getMessage();
			if ( msg.contains( key ) ) {
				msg = msg.replace( key, "******" );
			}

			wmc_log( msg );

			return "";
		}

		StringBuilder buffer = new StringBuilder();
		// Walk over each line of the response.
		String line;
		while ( ( line = input.readLine() ) != null ) {
			buffer.append( line );
		}

		input.close();

		return buffer.toString();
	}

	/**
	 * Log stuffs.
	 *
	 * @param message The message to log.
	 */
	private void wmc_log(String message) {
		this.wmc_log( message, 1 );
	}

	/**
	 * Logs to the debug log.
	 * @param message The message
	 */
	private void debug_log( String message ) {
		if ( isDebug() ) {
			this.wmc_log( message, 1 );
		}
	}
	/**
	 * Logs to the debug log.
	 * @param message The message
	 * @param level The log leve.
	 */
	private void debug_log( String message, Integer level ) {
		if ( isDebug() ) {
			this.wmc_log( message, level );
		}
	}

	/**
	 * Log stuffs.
	 *
	 * @param message The message to log.
	 * @param level The level to log it at.
	 */
	private void wmc_log(String message, Integer level) {

		if ( ! isDebug() ) {
			return;
		}

		switch ( level ) {
			case 1:
				this.getLogger().info( message );
				break;
			case 2:
				this.getLogger().warn( message );
				break;
			case 3:
				this.getLogger().error( message );
				break;
		}
	}

	/**
	 * Determines if the user is a paid user or not.
	 *
	 * @param player A player object
	 * @return If the user is a paid player.
	 */
	private boolean isPaidUser(ServerPlayer player) {
		String playerName = player.name();
		String playerUUID = player.uniqueId().toString().replace( "-", "" );
		String playerKeyBase = playerName + ':' + playerUUID + ':';
		String validPlayerKey = playerKeyBase + true;
		String invalidPlayerKey = playerKeyBase + false;

		// Check if server is in online mode.
		if (onlineMode) {
			wmc_log( getLocalizedLogString(getSystemOrDefaultLocale(), "online_mode"), 3 );
			return true;
		}

		if (!proxy) {
			wmc_log( getLocalizedLogString(getSystemOrDefaultLocale(), "offline_mode"), 3 );
			return false;
		}

		// Check the base pattern, if it exists, return if the player is valid or not.
		// Doing so should save on many if/else statements
		if (PlayersMap.toString().contains( playerKeyBase ) ) {
			boolean valid = PlayersMap.contains( validPlayerKey );
			if (!valid) {
				if(player.isOnline()) player.sendMessage(getLocalizedGeneralText(player.locale(), "speak_with_admin"));
				wmc_log(getLocalizedLogString(getSystemOrDefaultLocale(), "offline_mode_not_support"), 3);
			}

			return valid;
		}

		debug_log( getLocalizedLogString(getSystemOrDefaultLocale(), "not_in_the_key_set") + NL + PlayersMap.toString() );

		try {
			URL mojangUrl = new URL("https://api.mojang.com/users/profiles/minecraft/" +  playerName);
			InputStream inputStream = mojangUrl.openStream();
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(inputStream);
			String apiResponse = scanner.next();

			debug_log(
				getLocalizedLogString(getSystemOrDefaultLocale(), "stream_data") + NL +
				inputStream.toString() + NL +
				apiResponse + NL +
				playerName + NL +
				playerUUID
			);

			if ( ! apiResponse.contains( playerName ) ) {
				PlayersMap.add( invalidPlayerKey );
				throw new IOException(getLocalizedLogString(getSystemOrDefaultLocale(), "name_not_exist"));
			}

			if ( ! apiResponse.contains( playerUUID ) ) {
				//if Username exists but is using the offline uuid(doesn't match mojang records) throw IOException and add player to the list as cracked
				PlayersMap.add( invalidPlayerKey );
				throw new IOException(getLocalizedLogString(getSystemOrDefaultLocale(), "name_not_match_uuid"));
			}

			PlayersMap.add( validPlayerKey );
			debug_log( PlayersMap.toString() );
			return true;
		} catch ( MalformedURLException urlException ) {
			debug_log(getLocalizedLogString(getSystemOrDefaultLocale(), "malformed_url") + urlException.getMessage(), 3 );
			if(player.isOnline()) player.sendMessage(getLocalizedGeneralText(player.locale(), "mojang_api_error"));
		} catch ( IOException e ) {
			debug_log( getLocalizedLogString(getSystemOrDefaultLocale(), "map") + PlayersMap.toString() );
			debug_log( getLocalizedLogString(getSystemOrDefaultLocale(), "message_when_getting") + e.getMessage(), 3 );
			if(player.isOnline()) player.sendMessage(getLocalizedGeneralText(player.locale(), "speak_with_admin"));
		}

		// Default to false, worst case they have to run this twice.
		return false;
	}
}
