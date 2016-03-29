/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package net.fs.client;

import com.alibaba.fastjson.JSONObject;

import net.fs.rudp.Route;
import net.fs.utils.MLog;

import org.pcap4j.core.Pcaps;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;

public class ClientNoUI implements ClientUII{
	public boolean osx_fw_pf = false;

	public boolean osx_fw_ipfw = false;

	boolean success_firewall_windows = true;

	boolean success_firewall_osx = true;

	MapClient mapClient;
	
	ClientConfig config;
	
	String configFilePath="client_config.json";
	
	ClientNoUI() throws Exception {
		loadConfig();
		Route.localDownloadSpeed=config.downloadSpeed;
		Route.localUploadSpeed=config.uploadSpeed;

		//tryit();

		mapClient=new MapClient(null,false);
		MLog.info("sssssssss");
		mapClient.setMapServer("sundp.me", config.getServerPort(), config.getRemotePort(), null, null, config.isDirect_cn(), config.getProtocal().equals("tcp"),
				null);
		MLog.info("sssssssss");
		mapClient.closeAndTryConnect();
		PortMapManager portMapManager=new PortMapManager(mapClient);
		/*MapRule mapRule = new MapRule();
		mapRule.dst_port = 8989;
		mapRule.listen_port = 8989;
		mapRule.name = "ss";
		portMapManager.addMapRule(mapRule);*/
	}
	static boolean b1= false;
/*
	private void tryit() {

		checkFireWallOn();
		if (!success_firewall_windows) {
			MLog.println("启动windows防火墙失败,请先运行防火墙服务.");
			// System.exit(0);
		}
		if (!success_firewall_osx) {
			MLog.println("启动ipfw/pf防火墙失败,请先安装.");
			//System.exit(0);
		}

		Thread thread = new Thread() {
			public void run() {
				try {
					Pcaps.findAllDevs();
					b1 = true;
				} catch (Exception e3) {
					e3.printStackTrace();

				}
			}
		};
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//JOptionPane.showMessageDialog(mainFrame,System.getProperty("os.name"));
		if (!b1) {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						String msg = "启动失败,请先安装libpcap,否则无法使用tcp协议";
						if (systemName.contains("windows")) {
							msg = "启动失败,请先安装winpcap,否则无法使用tcp协议";
						}
						if (isVisible) {
							mainFrame.setVisible(true);
							JOptionPane.showMessageDialog(mainFrame, msg);
						}
						MLog.println(msg);
						if (systemName.contains("windows")) {
							try {
								Process p = Runtime.getRuntime().exec("winpcap_install.exe", null);
							} catch (IOException e) {
								e.printStackTrace();
							}
							tcpEnable=false;
							//System.exit(0);
						}
					}

				});
			} catch (InvocationTargetException e2) {
				e2.printStackTrace();
			} catch (InterruptedException e2) {
				e2.printStackTrace();
			}
		}
	}
*/

	public void setMessage(String message){
		MLog.info("状态: "+message);
	}
	
	ClientConfig loadConfig(){
		ClientConfig cfg=new ClientConfig();
		if(!new File(configFilePath).exists()){
			JSONObject json=new JSONObject();
			try {
				saveFile(json.toJSONString().getBytes(), configFilePath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			String content=readFileUtf8(configFilePath);
			JSONObject json=JSONObject.parseObject(content);
			cfg.setServerAddress(json.getString("server_address"));
			cfg.setServerPort(json.getIntValue("server_port"));
			cfg.setRemotePort(json.getIntValue("remote_port"));
			if(json.containsKey("direct_cn")){
				cfg.setDirect_cn(json.getBooleanValue("direct_cn"));
			}
			cfg.setDownloadSpeed(json.getIntValue("download_speed"));
			cfg.setUploadSpeed(json.getIntValue("upload_speed"));
			if(json.containsKey("socks5_port")){
				cfg.setSocks5Port(json.getIntValue("socks5_port"));
			}
			config=cfg;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cfg;
	}
		
	public static String readFileUtf8(String path) throws Exception{
		String str=null;
		FileInputStream fis=null;
		DataInputStream dis=null;
		try {
			File file=new File(path);

			int length=(int) file.length();
			byte[] data=new byte[length];

			fis=new FileInputStream(file);
			dis=new DataInputStream(fis);
			dis.readFully(data);
			str=new String(data,"utf-8");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			if(fis!=null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(dis!=null){
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return str;
	}
	
	void saveFile(byte[] data,String path) throws Exception{
		FileOutputStream fos=null;
		try {
			fos=new FileOutputStream(path);
			fos.write(data);
		} catch (Exception e) {
			throw e;
		} finally {
			if(fos!=null){
				fos.close();
			}
		}
	}
	
	public void updateUISpeed(int conn,int downloadSpeed,int uploadSpeed){
//		String string="连接数:"+conn+" 下载:"+Tools.getSizeStringKB(downloadSpeed)+"/S"
//				+" 上传:"+Tools.getSizeStringKB(uploadSpeed)+"/S";
//		if(downloadSpeedField!=null){
//			downloadSpeedField.setText(string);
//		}
	}

	JButton createButton(String name){
		JButton button=new JButton(name);
		button.setMargin(new Insets(0,5,0,5));
		button.setFocusPainted(false);
		return button;
	}
	
	
	void initUI(){
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {
				Font font = new Font("宋体",Font.PLAIN,12);   
				UIManager.put("ToolTip.font",font);   
				UIManager.put("Table.font",font);   
				UIManager.put("TableHeader.font",font);   
				UIManager.put("TextField.font",font);   
				UIManager.put("ComboBox.font",font);   
				UIManager.put("TextField.font",font);   
				UIManager.put("PasswordField.font",font);
				UIManager.put("TextArea.font,font",font);
				UIManager.put("TextPane.font",font);
				UIManager.put("EditorPane.font",font);   
				UIManager.put("FormattedTextField.font",font);   
				UIManager.put("Button.font",font);   
				UIManager.put("CheckBox.font",font);   
				UIManager.put("RadioButton.font",font);   
				UIManager.put("ToggleButton.font",font);   
				UIManager.put("ProgressBar.font",font);   
				UIManager.put("DesktopIcon.font",font);   
				UIManager.put("TitledBorder.font",font);   
				UIManager.put("Label.font",font);   
				UIManager.put("List.font",font);   
				UIManager.put("TabbedPane.font",font);   
				UIManager.put("MenuBar.font",font);   
				UIManager.put("Menu.font",font);   
				UIManager.put("MenuItem.font",font);   
				UIManager.put("PopupMenu.font",font);   
				UIManager.put("CheckBoxMenuItem.font",font);
				UIManager.put("RadioButtonMenuItem.font",font);
				UIManager.put("Spinner.font",font);
				UIManager.put("Tree.font",font);
				UIManager.put("ToolBar.font",font);
				UIManager.put("OptionPane.messageFont",font);
				UIManager.put("OptionPane.buttonFont",font);
				
				ToolTipManager.sharedInstance().setInitialDelay(200);
			}

		});
	}

	@Override
	public boolean login() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateNode(boolean testSpeed) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOsx_fw_pf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOsx_fw_ipfw() {
		// TODO Auto-generated method stub
		return false;
	}


	void checkFireWallOn() {
		String systemName = System.getProperty("os.name").toLowerCase();
		if (systemName.contains("os x")) {
			String runFirewall = "ipfw";
			try {
				final Process p = Runtime.getRuntime().exec(runFirewall, null);
				osx_fw_ipfw = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			runFirewall = "pfctl";
			try {
				final Process p = Runtime.getRuntime().exec(runFirewall, null);
				osx_fw_pf = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
			success_firewall_osx = osx_fw_ipfw | osx_fw_pf;
		} else if (systemName.contains("linux")) {
			String runFirewall = "service iptables start";

		} else if (systemName.contains("windows")) {
			String runFirewall = "netsh advfirewall set allprofiles state on";
			Thread standReadThread = null;
			Thread errorReadThread = null;
			try {
				final Process p = Runtime.getRuntime().exec(runFirewall, null);
				standReadThread = new Thread() {
					public void run() {
						InputStream is = p.getInputStream();
						BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(is));
						while (true) {
							String line;
							try {
								line = localBufferedReader.readLine();
								if (line == null) {
									break;
								} else {
									if (line.contains("Windows")) {
										success_firewall_windows = false;
									}
								}
							} catch (IOException e) {
								e.printStackTrace();
								//error();
								break;
							}
						}
					}
				};
				standReadThread.start();

				errorReadThread = new Thread() {
					public void run() {
						InputStream is = p.getErrorStream();
						BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(is));
						while (true) {
							String line;
							try {
								line = localBufferedReader.readLine();
								if (line == null) {
									break;
								} else {
									System.out.println("error" + line);
								}
							} catch (IOException e) {
								e.printStackTrace();
								//error();
								break;
							}
						}
					}
				};
				errorReadThread.start();
			} catch (IOException e) {
				e.printStackTrace();
				success_firewall_windows = false;
				//error();
			}

			if (standReadThread != null) {
				try {
					standReadThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (errorReadThread != null) {
				try {
					errorReadThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	void checkQuanxian() {
		String systemName = System.getProperty("os.name").toLowerCase();
		if (systemName.contains("windows")) {
			boolean b = false;
			File file = new File(System.getenv("WINDIR") + "\\test.file");
			//System.out.println("kkkkkkk "+file.getAbsolutePath());
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			b = file.exists();
			file.delete();

			if (!b) {
				//mainFrame.setVisible(true);
				MLog.println("请以管理员身份运行,否则可能无法正常工作! ");
//                System.exit(0);
			}
		}
	}

}
