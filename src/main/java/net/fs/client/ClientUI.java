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
import net.fs.utils.LogOutputStream;
import net.fs.utils.MLog;
import net.fs.utils.Tools;
import net.miginfocom.swing.MigLayout;

import org.pcap4j.core.Pcaps;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.*;

public class ClientUI implements ClientUII, WindowListener {

    MapClient mapClient;

    ClientConfig config = null;

    String configFilePath = "client_config.json";

    int serverVersion = -1;

    int localVersion = 2;

    boolean checkingUpdate = false;

    String domain = "";

    String homeUrl;

    public static ClientUI ui;

    String errorMsg = "保存失败请检查输入信息!";

    MapRuleListModel model;

    Exception capException = null;
    boolean b1 = false;

    String systemName = null;

    String updateUrl;
    
    boolean min=false;
    
    LogFrame logFrame;
    
    LogOutputStream los;
    
    boolean tcpEnable=true;

    {
        domain = "ip4a.com";
        homeUrl = "http://www.ip4a.com/?client_fs";
        updateUrl = "http://fs.d1sm.net/finalspeed/update.properties";
    }

    ClientUI(final boolean isVisible,boolean min) {
    	this.min=min;

        if(isVisible){
        	 los=new LogOutputStream(System.out);
             System.setOut(los);
             System.setErr(los);
        }
        
        
        systemName = System.getProperty("os.name").toLowerCase();
        MLog.info("System: " + systemName + " " + System.getProperty("os.version"));
        ui = this;
        loadConfig();



        model = new MapRuleListModel();


        JPanel p9 = new JPanel();
        p9.setLayout(new MigLayout("insets 1 0 3 0 "));
        JButton button_add = createButton("添加");
        p9.add(button_add);
        JButton button_edit = createButton("修改");
        p9.add(button_edit);
        JButton button_remove = createButton("删除");
        p9.add(button_remove);

        JPanel pa = new JPanel();
        pa.setBorder(BorderFactory.createTitledBorder("服务器"));
        pa.setLayout(new MigLayout("insets 0 0 0 0"));
        JPanel p1 = new JPanel();
        p1.setLayout(new MigLayout("insets 0 0 0 0"));
        pa.add(p1, "wrap");
        p1.add(new JLabel("地址:"), "width 50::");


        JPanel panelr = new JPanel();
        pa.add(panelr, "wrap");
        panelr.setLayout(new MigLayout("insets 0 0 0 0"));
        panelr.add(new JLabel("传输协议:"));


        JPanel sp = new JPanel();
        sp.setBorder(BorderFactory.createTitledBorder("物理带宽"));
        sp.setLayout(new MigLayout("insets 5 5 5 5"));
        JPanel pa1 = new JPanel();
        sp.add(pa1, "wrap");
        pa1.setLayout(new MigLayout("insets 0 0 0 0"));
        pa1.add(new JLabel("下载:"), "width ::");

        JButton button_set_speed = createButton("设置带宽");
        pa1.add(button_set_speed);

        
        JPanel pa2 = new JPanel();
        sp.add(pa2, "wrap");
        pa2.setLayout(new MigLayout("insets 0 0 0 0"));
        pa2.add(new JLabel("上传:"), "width ::");

        
        JPanel sp2 = new JPanel();
        sp2.setLayout(new MigLayout("insets 0 0 0 0"));

        final JCheckBox cb=new JCheckBox("开机启动",config.isAutoStart());
        sp2.add(cb, "align center");
		cb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                config.setAutoStart(cb.isSelected());
                setAutoRun(config.isAutoStart());
            }

        });
		JButton button_show_log=createButton("显示日志");
		sp2.add(button_show_log, "wrap");
		button_show_log.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (logFrame == null) {
                    logFrame = new LogFrame(ui);
                    logFrame.setSize(700, 400);
                    logFrame.setLocationRelativeTo(null);
                    los.addListener(logFrame);

                    if (los.getBuffer() != null) {
                        logFrame.showText(los.getBuffer().toString());
                        los.setBuffer(null);
                    }
                }
                logFrame.setVisible(true);
            }
        });

        JPanel p4 = new JPanel();
        p4.setLayout(new MigLayout("insets 5 0 0 0 "));
        JButton button_save = createButton("确定");
        p4.add(button_save);

        JButton button_exit = createButton("退出");
        p4.add(button_exit);
        button_exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        updateUISpeed(0, 0, 0);
        setMessage(" ");

        String server_addressTxt = config.getServerAddress();
        if (config.getServerAddress() != null && !config.getServerAddress().equals("")) {
            if (config.getServerPort() != 150
                    && config.getServerPort() != 0) {
                server_addressTxt += (":" + config.getServerPort());
            }
        }


        if (config.getRemoteAddress() != null && !config.getRemoteAddress().equals("") && config.getRemotePort() > 0) {
            String remoteAddressTxt = config.getRemoteAddress() + ":" + config.getRemotePort();
        }

        int width = 500;
        if (systemName.contains("os x")) {
            width = 600;
        }
        //mainFrame.setSize(width, 380);


        boolean tcpEnvSuccess=true;

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
        	tcpEnvSuccess=false;
            try {
                SwingUtilities.invokeAndWait(new Runnable() {

                    @Override
                    public void run() {
                        String msg = "启动失败,请先安装libpcap,否则无法使用tcp协议";
                        if (systemName.contains("windows")) {
                            msg = "启动失败,请先安装winpcap,否则无法使用tcp协议";
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


        try {
            mapClient = new MapClient(this,tcpEnvSuccess);
        } catch (final Exception e1) {
            e1.printStackTrace();
            capException = e1;
            //System.exit(0);
        }

        //mapClient.setUi(this);

        mapClient.setMapServer(config.getServerAddress(), config.getServerPort(), config.getRemotePort(), null, null, config.isDirect_cn(), config.getProtocal().equals("tcp"),
                null);

        Route.es.execute(new Runnable() {

            @Override
            public void run() {
                checkUpdate();
            }
        });

    }

    void checkQuanxian() {
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


    void setSpeed(int downloadSpeed, int uploadSpeed) {
        config.setDownloadSpeed(downloadSpeed);
        config.setUploadSpeed(uploadSpeed);
        int s1 = (int) ((float) downloadSpeed * 1.1f);
        int s2 = (int) ((float) uploadSpeed * 1.1f);
        Route.localDownloadSpeed = downloadSpeed;
        Route.localUploadSpeed = config.uploadSpeed;

    }


    void openUrl(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }

    ClientConfig loadConfig() {
        ClientConfig cfg = new ClientConfig();
        if (!new File(configFilePath).exists()) {
            JSONObject json = new JSONObject();
            try {
                saveFile(json.toJSONString().getBytes(), configFilePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            String content = readFileUtf8(configFilePath);
            JSONObject json = JSONObject.parseObject(content);
            cfg.setServerAddress(json.getString("server_address"));
            cfg.setServerPort(json.getIntValue("server_port"));
            cfg.setRemotePort(json.getIntValue("remote_port"));
            cfg.setRemoteAddress(json.getString("remote_address"));
            if (json.containsKey("direct_cn")) {
                cfg.setDirect_cn(json.getBooleanValue("direct_cn"));
            }
            cfg.setDownloadSpeed(json.getIntValue("download_speed"));
            cfg.setUploadSpeed(json.getIntValue("upload_speed"));
            if (json.containsKey("socks5_port")) {
                cfg.setSocks5Port(json.getIntValue("socks5_port"));
            }
            if (json.containsKey("protocal")) {
                cfg.setProtocal(json.getString("protocal"));
            }
            if (json.containsKey("auto_start")) {
                cfg.setAutoStart(json.getBooleanValue("auto_start"));
            }
            config = cfg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cfg;
    }


    public static String readFileUtf8(String path) throws Exception {
        String str = null;
        FileInputStream fis = null;
        DataInputStream dis = null;
        try {
            File file = new File(path);

            int length = (int) file.length();
            byte[] data = new byte[length];

            fis = new FileInputStream(file);
            dis = new DataInputStream(fis);
            dis.readFully(data);
            str = new String(data, "utf-8");

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return str;
    }

    void saveFile(byte[] data, String path) throws Exception {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(data);
        } catch (Exception e) {
            if (systemName.contains("windows")) {
                JOptionPane.showMessageDialog(null, "保存配置文件失败,请尝试以管理员身份运行! " + path);
                System.exit(0);
            }
            throw e;
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    JButton createButton(String name) {
        JButton button = new JButton(name);
        button.setMargin(new Insets(0, 5, 0, 5));
        button.setFocusPainted(false);
        return button;
    }

    boolean haveNewVersion() {
        return serverVersion > localVersion;
    }

    public void checkUpdate() {
        for (int i = 0; i < 3; i++) {
            checkingUpdate = true;
            try {
                Properties propServer = new Properties();
                HttpURLConnection uc = Tools.getConnection(updateUrl);
                uc.setUseCaches(false);
                InputStream in = uc.getInputStream();
                propServer.load(in);
                serverVersion = Integer.parseInt(propServer.getProperty("version"));
                break;
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(3 * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            } finally {
                checkingUpdate = false;
            }
        }
        if (this.haveNewVersion()) {
            MLog.info("发现新版本,立即更新吗?");
        }

    }

	public static void setAutoRun(boolean run) {
		String s = new File(".").getAbsolutePath();
		String currentPaht = s.substring(0, s.length() - 1);
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(currentPaht, "\\");
		while (st.hasMoreTokens()) {
			sb.append(st.nextToken());
			sb.append("\\\\");
		}
		ArrayList<String> list = new ArrayList<String>();
		list.add("Windows Registry Editor Version 5.00");
		String name="fsclient";
//		if(PMClientUI.mc){
//			name="wlg_mc";
//		}
		if (run) {
			list.add("[HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run]");
			list.add("\""+name+"\"=\"" + sb.toString() + "finalspeedclient.exe -min" + "\"");
		} else {
			list.add("[HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run]");
			list.add("\""+name+"\"=-");
		}

		File file = null;
		try {
			file = new File("import.reg");
			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);
			for (int i = 0; i < list.size(); i++) {
				String ss = list.get(i);
				if (!ss.equals("")) {
					pw.println(ss);
				}
			}
			pw.flush();
			pw.close();
			Process p = Runtime.getRuntime().exec("regedit /s " + "import.reg");
			p.waitFor();
		} catch (Exception e1) {
			// e1.printStackTrace();
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }


    @Override
    public void setMessage(String message) {

    }

    @Override
    public void updateUISpeed(int connNum, int downSpeed, int upSpeed) {

    }

    @Override
    public boolean login() {
        return false;
    }


    @Override
    public boolean updateNode(boolean testSpeed) {
        return true;

    }

    @Override
    public boolean isOsx_fw_pf() {
        return false;
    }

    @Override
    public boolean isOsx_fw_ipfw() {
        return false;
    }

}
