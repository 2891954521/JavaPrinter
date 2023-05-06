package com.print.config;

import com.print.entity.config.ConfigFile;
import com.print.utils.FileUtil;
import com.print.utils.Logger;
import com.print.utils.YamlUtil;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 静态配置信息
 */
public class Config{
	
	public static File CONFIG_FILE;
	
	public Map<String, Boolean> boolSetting;
	
	public ConfigFile configFile;
	
	public Config(){
		resolveConfigFile();
		boolSetting = new HashMap<>();
		YamlUtil.dumpBool(boolSetting, configFile);
	}
	
	/**
	 * 读取配置文件
	 */
	public void resolveConfigFile(){

		if(CONFIG_FILE == null){
			String path = FileUtil.getJarPath();
			if(path == null){
				Logger.log("无法加载配置文件！使用默认配置");
				configFile = new ConfigFile();
				return;
			}
			CONFIG_FILE = new File(path, "print-conf.yml");
		}

		if(!CONFIG_FILE.exists()){
			Logger.log("配置文件不存在，使用默认配置");
			configFile = new ConfigFile();
			FileUtil.writeFile(CONFIG_FILE, YamlUtil.dump(configFile));
			return;
		}

		try{
			Representer representer = new Representer(new DumperOptions());
			representer.getPropertyUtils().setSkipMissingProperties(true);
			Yaml yaml = new Yaml(new Constructor(ConfigFile.class), representer);
			configFile = yaml.load(new InputStreamReader(new FileInputStream(CONFIG_FILE), StandardCharsets.UTF_8));
		}catch(IOException | IllegalArgumentException e){
			Logger.log("配置文件格式错误，将使用默认配置", e);
			configFile = new ConfigFile();
		}
	}
}