package edu.teco.dnd.module.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import edu.teco.dnd.eclipse.EclipseUtil;

public class JsonConfig extends ConfigReader {
	private String name;
	private UUID uuid;
	private NetworkInterface[] listen;
	private NetworkInterface[] announce;
	private NetworkInterface[] multicast;
	private Set<BlockType> allowedBlocks;

	private static final Logger LOGGER = LogManager
			.getLogger(EclipseUtil.class);
	Gson gson = new Gson();

	public JsonConfig() {
	}

	public JsonConfig(String path) throws IOException {
		this.restore(path);
	}

	public void setTo(JsonConfig oldConf) {
		this.name = oldConf.name;
		this.uuid = oldConf.uuid;
		this.listen = oldConf.listen;
		this.announce = oldConf.announce;
		this.multicast = oldConf.multicast;
		this.allowedBlocks = oldConf.allowedBlocks;
	}

	@Override
	public boolean restore(String path) {
		FileReader reader = null;
		try {
			reader = new FileReader(path);
			setTo((JsonConfig) gson.fromJson(reader, this.getClass()));
		} catch (FileNotFoundException e) {
			LOGGER.catching(e);
			return false;
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
		return true;
	}

	@Override
	public boolean save(String path) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(path);
			gson.toJson(this, writer);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				writer.close();
			} catch (Exception e) {
			}
		}
		return true;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public UUID getUuid() {
		return uuid;
	}

	@Override
	public NetworkInterface[] getListen() {
		return listen;
	}

	@Override
	public NetworkInterface[] getAnnounce() {
		return announce;
	}

	@Override
	public NetworkInterface[] getMulticast() {
		return multicast;
	}

	@Override
	public Set<BlockType> getAllowedBlocks() {
		return allowedBlocks;
	}

}
