// Copyright (c) 2015 D1SM.net
package net.fs.rudp.message;

public class MessageType {
	public static final short S_TYPE_DATA_MESSAGE =80;
	public static final short S_TYPE_CONNECT_MESSAGE_1 =71;
	public static final short S_TYPE_CONNECT_MESSAGE_2 =72;
	public static final short S_TYPE_CONNECT_MESSAGE_3 =73;
	public static final short S_TYPE_CLOSE_MESSAGE_STREAM =75;
	public static final short S_TYPE_CLOSE_MESSAGE_CONN =76;
	
	public static final short S_TYPE_ACK_MESSAGE =61;
	public static final short S_TYPE_LAST_READ_MESSAGE =65;
	public static final short S_TYPE_ACK_LIST_MESSAGE =60;
	
	public static final short S_TYPE_ASK_FILL_MESSAGE =63;
	public static final short S_TYPE_RE_SEND_MESSAGE =62;
	
	public static final short S_TYPE_ACK_LIST_MESSAGE_TUN =66;
	

	public static final short S_TYPE_UDP_TUN_DATA_MESSAGE =90;
	public static final short S_TYPE_UDP_TUN_OPEN_MESSAGE =91;
	public static final short S_TYPE_UDP_TUN_CLOSE_MESSAGE =92;
	
	public static final short S_TYPE_CLEAN_MESSAGE_1 =225;
	public static final short S_TYPE_CLEAN_MESSAGE_2 =226;
	
	public static final short S_TYPE_REG_MESSAGE =101;
	public static final short S_TYPE_REG_MESSAGE_2 =102;
	public static final short S_TYPE_EXIT_MESSAGE =111;
	public static final short S_TYPE_EXIT_MESSAGE_2 =112;
	public static final short S_TYPE_PUB_ADD_MESSAGE =141;
	public static final short S_TYPE_PUB_ADD_MESSAGE_2 =142;
	public static final short S_TYPE_PUB_DEL_MESSAGE =151;
	public static final short S_TYPE_PUB_DEL_MESSAGE_2 =152;
	public static final short S_TYPE_S_LIVE_MESSAGE =131;
	public static final short S_TYPE_S_LIVE_MESSAGE_2 =132;
	public static final short S_TYPE_GET_S_NODE_MESSAGE =161;
	public static final short S_TYPE_GET_S_NODE_MESSAGE_2 =162;
	public static final short S_TYPE_JOIN_DB_FAIL_MESSAGE_1 =168;
	public static final short S_TYPE_JOIN_DB_FAIL_MESSAGE_2 =169;
	public static final short S_TYPE_TIME_SYN_MESSAGE_1 =175;
	public static final short S_TYPE_TIME_SYN_MESSAGE_2 =176;
	public static final short S_TYPE_ADV_CS_MESSAGE_1 =178;
	public static final short S_TYPE_ADV_CS_MESSAGE_2 =179;
	
	public static final short S_TYPE_CAST_GROUP_MESSAGE =181;
	public static final short S_TYPE_CAST_GROUP_MESSAGE_2 =182;
	public static final short S_TYPE_CAST_GROUP_RANDOM_MESSAGE =191;
	public static final short S_TYPE_CAST_GROUP_RANDOM_MESSAGE_2 =192;
	public static final short S_TYPE_CAST_GROUP_RANDOM_MESSAGE_3 =193;
	
	public static final short sType_Assist_RegMessage=500;
	public static final short sType_Assist_RegMessage2=501;
	public static final short sType_Assist_PingMessage1=510;
	public static final short sType_Assist_PingMessage2=511;
	public static final short sType_ReversePingMessage1=515;
	public static final short sType_ReversePingMessage2=516;
	public static final short sType_ReversePingMessage3=517;
	public static final short sType_ReverseConnTCPMessage1=518;
	public static final short sType_ReverseConnTCPMessage2=519;
	public static final short sType_Assist_OtherOutAddressMessage=520;
	public static final short sType_Assist_OtherOutAddressMessage2=521;
	public static final short sType_Assist_OtherOutAddressMessage3=522;
	public static final short sType_Assist_OtherOutAddressMessage4=523;
	
	public static final short sType_Assist_LiveMessage =4125;
	public static final short sType_Assist_LiveMessage2=4126;
	public static final short sType_Assist_ExitMessage=540;
	public static final short sType_Assist_ExitMessage2=541;
	
	
	public static final short sType_DB_CastAddMessage=601;
	public static final short sType_DB_CastAddMessage2=602;
	public static final short sType_DB_CastRemoveMessage=701;
	public static final short sType_DB_CastRemoveMessage2=702;
	public static final short sType_DB_SourceSearchMessage=711;
	public static final short sType_DB_SourceSearchMessage2=712;
	public static final short sType_DB_SourceSumMessage1=715;
	public static final short sType_DB_SourceSumMessage2=716;
	
	public static final short sType_getOutAddressMessage=1181;
	public static final short sType_getOutAddressMessage2=1182;
	
	public static final short sType_PingMessage=301;
	public static final short sType_PingMessage2=302;
	
	public static final short sType_PingMessagec=311;
	public static final short sType_PingMessagec2=312;
	
	public static final short sType_PingMessager=321;
	public static final short sType_PingMessager2=322;

	private MessageType() {
	}
}
