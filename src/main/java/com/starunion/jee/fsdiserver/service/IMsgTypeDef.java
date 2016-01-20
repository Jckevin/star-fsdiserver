package com.starunion.jee.fsdiserver.service;

public interface IMsgTypeDef {
	
	static final short SUCCESS = 0;
	static final short FAILURE = -1;
	
	static final String EVENT_CUSTOM = "CUSTOM";
	static final String EVENT_HEARBEAT = "HEARTBEAT";
	static final String EVENT_CHANNEL_CALLSTATE = "CHANNEL_CALLSTATE";
	static final String EVENT_CHANNEL_PARK = "CHANNEL_PARK";

	static final String TER_STATUS_UNREG = "0";
	static final String TER_STATUS_REG = "1";
	static final String TER_STATUS_FRING = "2";
	static final String TER_STATUS_RING = "3";
	static final String TER_STATUS_ANSWER = "4";
	
	static final String FS_MEET_NORMAL_TYPE = "gcallnormalls";
	static final String FS_MEET_MUTE_TYPE = "gcallmutels";
	static final String FS_MEET_PLAY_TYPE = "gcallplayls";
	static final String FS_MEET_PLAYER_TYPE = "music";

	static final String DISP_LOGIN = "login";
	static final String DISP_SHOW_USERS = "showuserlist";

	static final String FUNCODE_PICKUP = "40";
	static final String FORBID_ON = "1";
	static final String FORBID_OFF = "0";

	static final String DISP_TAIL_SUCC = ":OK\r\n";
	static final String DISP_TAIL_FAIL = ":FAIL\r\n";
	static final String DISP_FSCMD_TAIL = "\n\n";

	static final String DISP_SHOW_DUSERS = "showdispatchuserlist";
	static final String DISP_SHOW_PEERS = "showallpeers";
	static final String DISP_SHOW_NLIST = "shownightlist";
	static final String DISP_SHOW_FLIST = "showstopuseextens";
	static final String DISP_SHOW_FLIST_PRE = "stopuseexten";
	static final String DISP_SHOW_CAMERS = "showvideolist";
	static final String DISP_SHOW_CAMERS_PRE = "videolist";
	static final String DISP_SHOW_BROAD_PLAY = "showplaylist";
	static final String DISP_SHOW_BROAD_PLAY_PRE = "playlist";

	static final String DISP_KEEP_ALIVE = "keepalive";
	static final String DISP_DLOGIN = "dispatchlogin";
	static final String DISP_THIRD_PCALL = "call";
	static final String DISP_MONITOR_CALL = "monitor";
	static final String DISP_INSERT_CALL = "insert";
	static final String DISP_BRIDGE_CALL = "bridge";
	static final String DISP_DEMOLION_CALL = "demolition";
	static final String DISP_PICKUP_CALL = "pickup";
	static final String DISP_TRANS_CALL = "transfer";
	static final String DISP_HANGUP_CALL = "hangup";
	static final String DISP_HANGUPMEET_CALL = "hangupmeet";
	static final String DISP_NIGHT_CALL = "night";
	static final String DISP_UNIGHT_CALL = "stopnight";
	static final String DISP_FORBID_CALL = "stopuse";
	static final String DISP_RECORD_CALL = "record";
	static final String DISP_PAGE_CALL = "page";
	static final String DISP_PAGE_CALLTYPE_MEET = "1";
	static final String DISP_PAGE_CALLTYPE_BROAD = "0";
	static final String DISP_VPAGE_CALL = "videopage";

	// conference related
	static final String DISP_MEET_JOIN = "meetme";
	static final String DISP_MEET_KICK = "meetmekick";
	static final String DISP_MEET_KICKALL = "meetmekickall";
	static final String DISP_MEET_MUTE = "meetmemute";
	static final String DISP_MEET_UNMUTE = "meetmeunmute";
	// queue related
	static final String DISP_QUEUE_ANS = "queueanswer";
	static final String DISP_QUEUE_JOIN = "queuejoin";
	static final String DISP_QUEUE_LEAVE = "queueleave";

	// broadcast audio
	static final String DISP_BROAD_PLAY = "playfile";
	static final String DISP_BROAD_PLANMOD_NOW = "0";
	static final String DISP_BROAD_PLANMOD_FUTURE = "1";
	static final String DISP_BROAD_PLANMOD_DAY = "3";
	static final String DISP_BROAD_PLANMOD_WEEK = "4";
	static final String DISP_BROAD_PLAYMOD_TIMES = "0";
	static final String DISP_BROAD_PLAYMOD_TIMELEN = "1";
	static final String DISP_BROAD_PLAY_DEL = "deleteplaylist";
//	预约模式：0 立即
//	 * 1定时2每天3每周 播放模式：0次数播放1时长播放 
	// captureplay play
	static final String DISP_BROAD_FPLAY_ADD = "addcaptureplay";
	static final String DISP_BROAD_FPLAY_DEL = "deletecaptureplay";
	static final String DISP_BROAD_FPLAY_SHOW = "showcaptureplaylist";

	// intercom
	static final String DISP_TALKIE_CALL = "pageintercom";
	static final String DISP_TALKIE_SHOW = "showallintercoms";
	static final String DISP_TALKIE_MEM_SHOW = "showintercomlist";
	
	// send message event
	static final String DISP_SEND_MSG = "sendmsg";
	
	// gps info relevant
	static final String DISP_GPS_EXTEN_ADD = "extenmapadd";
	static final String DISP_GPS_EXTEN_DEL = "extenmapdel";
	static final String DISP_GPS_EXTEN_SHOW = "showextenmap";
	static final String DISP_GPS_EXTEN_SHOW_ALL = "showallextenmap";
	static final String DISP_GPS_EXTEN_NOTIFY = "extenmapdata";
	static final String DISP_GPS_EXTEN_TRAIL = "extenmaptrail";
	
	
	public final String ESL_EVENT_CUSTOM = "";
	public final String ESL_EVENT_CLONE = "";
	public final String ESL_EVENT_CHANNEL_CREATE = "";
	public final String ESL_EVENT_CHANNEL_DESTROY = "";
	public final String ESL_EVENT_CHANNEL_STATE = "";
	public final String ESL_EVENT_CHANNEL_CALLSTATE = "";
	public final String ESL_EVENT_CHANNEL_ANSWER = "";
	public final String ESL_EVENT_CHANNEL_HANGUP = "";
	public final String ESL_EVENT_CHANNEL_HANGUP_COMPLETE = "";
	public final String ESL_EVENT_CHANNEL_EXECUTE = "";
	public final String ESL_EVENT_CHANNEL_EXECUTE_COMPLETE = "";
	public final String ESL_EVENT_CHANNEL_HOLD = "";
	public final String ESL_EVENT_CHANNEL_UNHOLD = "";
	public final String ESL_EVENT_CHANNEL_BRIDGE = "";
	public final String ESL_EVENT_CHANNEL_UNBRIDGE = "";
	public final String ESL_EVENT_CHANNEL_PROGRESS = "";
	public final String ESL_EVENT_CHANNEL_PROGRESS_MEDIA = "";
	public final String ESL_EVENT_CHANNEL_OUTGOING = "";
	public final String ESL_EVENT_CHANNEL_PARK = "";
	public final String ESL_EVENT_CHANNEL_UNPARK = "";
	public final String ESL_EVENT_CHANNEL_APPLICATION = "";
	public final String ESL_EVENT_CHANNEL_ORIGINATE = "";
	public final String ESL_EVENT_CHANNEL_UUID = "";
	public final String ESL_EVENT_API = "";
	public final String ESL_EVENT_LOG = "";
	public final String ESL_EVENT_INBOUND_CHAN = "";
	public final String ESL_EVENT_OUTBOUND_CHAN = "";
	public final String ESL_EVENT_STARTUP = "";
	public final String ESL_EVENT_SHUTDOWN = "";
	public final String ESL_EVENT_PUBLISH = "";
	public final String ESL_EVENT_UNPUBLISH = "";
	public final String ESL_EVENT_TALK = "";
	public final String ESL_EVENT_NOTALK = "";
	public final String ESL_EVENT_SESSION_CRASH = "";
	public final String ESL_EVENT_MODULE_LOAD = "";
	public final String ESL_EVENT_MODULE_UNLOAD = "";
	public final String ESL_EVENT_DTMF = "";
	public final String ESL_EVENT_MESSAGE = "";
	public final String ESL_EVENT_PRESENCE_IN = "";
	public final String ESL_EVENT_NOTIFY_IN = "";
	public final String ESL_EVENT_PRESENCE_OUT = "";
	public final String ESL_EVENT_PRESENCE_PROBE = "";
	public final String ESL_EVENT_MESSAGE_WAITING = "";
	public final String ESL_EVENT_MESSAGE_QUERY = "";
	public final String ESL_EVENT_ROSTER = "";
	public final String ESL_EVENT_CODEC = "";
	public final String ESL_EVENT_BACKGROUND_JOB = "";
	public final String ESL_EVENT_DETECTED_SPEECH = "";
	public final String ESL_EVENT_DETECTED_TONE = "";
	public final String ESL_EVENT_PRIVATE_COMMAND = "";
	public final String ESL_EVENT_HEARTBEAT = "";
	public final String ESL_EVENT_TRAP = "";
	public final String ESL_EVENT_ADD_SCHEDULE = "";
	public final String ESL_EVENT_DEL_SCHEDULE = "";
	public final String ESL_EVENT_EXE_SCHEDULE = "";
	public final String ESL_EVENT_RE_SCHEDULE = "";
	public final String ESL_EVENT_RELOADXML = "";
	public final String ESL_EVENT_NOTIFY = "";
	public final String ESL_EVENT_PHONE_FEATURE = "";
	public final String ESL_EVENT_PHONE_FEATURE_SUBSCRIBE = "";
	public final String ESL_EVENT_SEND_MESSAGE = "";
	public final String ESL_EVENT_RECV_MESSAGE = "";
	public final String ESL_EVENT_REQUEST_PARAMS = "";
	public final String ESL_EVENT_CHANNEL_DATA = "";
	public final String ESL_EVENT_GENERAL = "";
	public final String ESL_EVENT_COMMAND = "";
	public final String ESL_EVENT_SESSION_HEARTBEAT = "";
	public final String ESL_EVENT_CLIENT_DISCONNECTED = "";
	public final String ESL_EVENT_SERVER_DISCONNECTED = "";
	public final String ESL_EVENT_SEND_INFO = "";
	public final String ESL_EVENT_RECV_INFO = "";
	public final String ESL_EVENT_RECV_RTCP_MESSAGE = "";
	public final String ESL_EVENT_CALL_SECURE = "";
	public final String ESL_EVENT_NAT = "";
	public final String ESL_EVENT_RECORD_START = "";
	public final String ESL_EVENT_RECORD_STOP = "";
	public final String ESL_EVENT_PLAYBACK_START = "";
	public final String ESL_EVENT_PLAYBACK_STOP = "";
	public final String ESL_EVENT_CALL_UPDATE = "";
	public final String ESL_EVENT_FAILURE = "";
	public final String ESL_EVENT_SOCKET_DATA = "";
	public final String ESL_EVENT_MEDIA_BUG_START = "";
	public final String ESL_EVENT_MEDIA_BUG_STOP = "";
	public final String ESL_EVENT_CONFERENCE_DATA_QUERY = "";
	public final String ESL_EVENT_CONFERENCE_DATA = "";
	public final String ESL_EVENT_CALL_SETUP_REQ = "";
	public final String ESL_EVENT_CALL_SETUP_RESULT = "";
	public final String ESL_EVENT_CALL_DETAIL = "";
	public final String ESL_EVENT_DEVICE_STATE = "";
	public final String ESL_EVENT_ALL = "";

}
