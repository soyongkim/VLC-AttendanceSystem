package com.hoho.android.usbserial.util;

import java.nio.ByteBuffer;

/**
 * Created by user on 2017-11-30.
 */
public class GeneratedMessages {
    public class UP_LINK_COMMAND {
        public static final int GCS_IDLE_COMMAND = 0;
        public static final int GCS_IDLE_COMMAND_DEV = 1;


    }

    public class DOWN_LINK_COMMAND {
        public static final int GCS_REPLY_ACK = 5;
        public static final int GCS_REPLY_NACK = 6;
    }

    // Up-Link Command
    public class GCS_IDLE_COMMAND extends UVLinkMessage
    {
        public GCS_IDLE_COMMAND()
        {
            uMessageId = 0;
        }

        @Override
        public byte[] SerializeBody() {
            return new byte[0];
        }

        @Override
        public void DeserializeBody(byte[] payload) {

        }
    }

    public class GCS_IDLE_COMMAND_DEV extends UVLinkMessage
    {
        public GCS_IDLE_COMMAND_DEV()
        {
            uMessageId = 1;
        }

        @Override
        public byte[] SerializeBody() {
            return new byte[0];
        }

        @Override
        public void DeserializeBody(byte[] payload) {

        }
    }

    // Down-Link Meesage
    public static class GCS_REPLY_ACK extends UVLinkMessage
    {
        public GCS_REPLY_ACK()
        {
            uMessageId = 5;
        }

        @Override
        public byte[] SerializeBody() {
            return new byte[0];
        }

        @Override
        public void DeserializeBody(byte[] payload) {

        }
    }

    public static class GCS_REPLY_NACK extends UVLinkMessage
    {
        private short uErr_code;

        public short getuErr_code()
        {
            return uErr_code;
        }

        public GCS_REPLY_NACK()
        {
            uMessageId = 6;
        }

        @Override
        public byte[] SerializeBody() {
            return new byte[0];
        }

        @Override
        public void DeserializeBody(byte[] payload) {
            ByteBuffer temp = ByteBuffer.wrap(payload);
            uErr_code = temp.getShort();
        }
    }

// IDLE 패킷 답장 ------------------------------------------------
    public static class GCS_FLIGHT_INFO_1 extends UVLinkMessage
    {
        final double rad2deg = 180 / Math.PI;
        double e9 = Math.pow(10, -9);

        // uGPS_Time이 uint형이 였는데 상관없나?
        private int uGps_time;
        private int uLatitude;
        private int uLongitude;
        private int uAltitude;
        private int uAlt_ground;
        private int uAlt_baro;
        private short uVN;
        private short uVE;
        private short uVD;
        private short uCAS;
        private short uRoll;
        private short uPitch;
        private short uYaw;
        private short uPsi_path;
        private short uPsi_compass;
        //ushort 였던 것들
        private short uFrame_time_us;
        private short uCamera_shutter;
        private short uUser_data1;

        /// <summary>
        /// GPS Pbn time (msec)
        /// </summary>
        public int getGps_time()
        {
            return uGps_time;
        }

        /// <summary>
        /// Latitude (2e-9rad)
        /// </summary>
        public double getLatitude()
        {
            return (uLatitude * rad2deg * e9 * 2);
        }

        /// <summary>
        /// Longitude (2e-9rad)
        /// </summary>
        public double getLongitude()
        {
            return (uLongitude * rad2deg * e9 * 2);
        }

        /// <summary>
        /// WGS-84 Altitude (cm)
        /// </summary>
        public float getAltitude()
        {
            return (uAltitude / 100.0f);
        }

        /// <summary>
        /// Altitude from ground (cm)
        /// </summary>
        public float getAlt_ground()
        {
            return (uAlt_ground / 100.0f);
        }

        /// <summary>
        /// Pressure altitude (cm)
        /// </summary>
        public float getAlt_baro()
        {
            return (uAlt_baro / 100.0f);
        }

        /// <summary>
        /// North ground velocity (cm/s)
        /// </summary>
        public float getVN()
        {
            return (uVN / 100.0f);
        }

        /// <summary>
        /// East ground velocity (cm/s)
        /// </summary>
        public float getVE()
        {
            return (uVE / 100.0f);
        }

        /// <summary>
        /// Down ground velocity (cm/s)
        /// </summary>
        public float getVD()
        {
            return (uVD / 100.0f);
        }

        /// <summary>
        /// Calibrated air speed (cm/s)
        /// </summary>
        public float getCAS()
        {
            return (uCAS / 100.0f);
        }

        /// <summary>
        /// Euler Angle (0.01deg)
        /// </summary>
        public float getRoll()
        {
            return (uRoll / 100.0f);
        }

        /// <summary>
        /// Euler Angle (0.01deg)
        /// </summary>
        public float getPitch()
        {
            return (uPitch / 100.0f);
        }

        /// <summary>
        /// Euler Angle (0.01deg)
        /// </summary>
        public float getYaw()
        {
            return (uYaw / 100.0f);
        }

        /// <summary>
        /// track angle (0.01deg)
        /// </summary>
        public float getPsi_path()
        {
            return (uPsi_path / 100.0f);
        }

        /// <summary>
        /// Heading by compass (0.01deg)
        /// </summary>
        public float getPsi_compass()
        {
            return (uPsi_compass / 100.0f);
        }

        public short getFrame_time_us()
        {
            return uFrame_time_us;
        }

        /// <summary>
        /// camera shutter pwm out
        /// </summary>
        public short getCamera_shutter()
        {
            return uCamera_shutter;
        }

        /// <summary>
        /// user defined data 1
        /// </summary>
        public short getUser_data1()
        {
            return uUser_data1;
        }

        public GCS_FLIGHT_INFO_1()
        {
            uMessageId = 7;
        }

        @Override
        public byte getuMessageId() {
            return super.getuMessageId();
        }

        public void DeserializeBody(byte[] s)
        {
            ByteBuffer buf = ByteBuffer.wrap(s);
            uGps_time = buf.getInt();
            uLatitude = buf.getInt();
            uLongitude = buf.getInt();
            uAltitude = buf.getInt();
            uAlt_ground = buf.getInt();
            uAlt_baro = buf.getInt();
            uVN = buf.getShort();
            uVE = buf.getShort();
            uVD = buf.getShort();
            uCAS = buf.getShort();
            uRoll = buf.getShort();
            uPitch = buf.getShort();
            uYaw = buf.getShort();
            uPsi_path = buf.getShort();
            uPsi_compass = buf.getShort();
            uFrame_time_us = buf.getShort();
            uCamera_shutter = buf.getShort();
            uUser_data1 = buf.getShort();
        }

        @Override
        public byte[] SerializeBody() {
            return new byte[0];
        }
    }

    public static class GCS_FLIGHT_INFO_2 extends UVLinkMessage
    {
        private float uPb_set;

        private short uSlip;
        private short uAlt_sonar;

        private int uH_cmd;
        private short uPhi_cmd;
        private short uThe_cmd;
        private short uPsi_cmd;
        private short uPsi_path_cmd;
        private short uV_cmd;

        private short uAileron;
        private short uElevator;
        private short uEngine;

        private short[] adc_data = new short[8];

        /// <summary>
        /// Pressure set for Base (hPa)
        /// </summary>
        public float getPb_set()
        {
            return uPb_set;
        }

        /// <summary>
        /// slip (0.25mg)
        /// </summary>
        public short getSlip()
        {
            return uSlip;
        }

        /// <summary>
        /// Altitude from sonar sensor (cm)
        /// </summary>
        public float getAlt_sonar()
        {
            return (uAlt_sonar / 100.0f);
        }

        /// <summary>
        /// Altitude command for autopilot (cm)
        /// </summary>
        public float getH_cmd()
        {
            return (uH_cmd / 100.0f);
        }

        /// <summary>
        /// Roll angle command for autopilot (0.01deg)
        /// </summary>
        public float getPhi_cmd()
        {
            return (uPhi_cmd / 100.0f);
        }

        /// <summary>
        /// Pitch angle command for autopilot (0.01deg)
        /// </summary>
        public float getThe_cmd()
        {
            return (uThe_cmd / 100.0f);
        }

        /// <summary>
        /// Heading angle command for autopilot (0.01deg)
        /// </summary>
        public float getPsi_cmd()
        {
            return (uPsi_cmd / 100.0f);
        }

        /// <summary>
        /// Track angle command for autopilot (0.01deg)
        /// </summary>
        public float getPsi_path_cmd()
        {
            return (uPsi_path_cmd / 100.0f);
        }

        /// <summary>
        /// Speed command for autopilot (cm/s)
        /// </summary>
        public float getV_cmd()
        {
            return (uV_cmd / 100.0f);
        }

        public short getAileron()
        {
            return uAileron;
        }

        public short getElevator()
        {
            return uElevator;
        }

        public short getEngine()
        {
            return uEngine;
        }

        public GCS_FLIGHT_INFO_2()
        {
            uMessageId = 8;
        }

        public void DeserializeBody(byte[] s)
        {
            ByteBuffer buf = ByteBuffer.wrap(s);
            uPb_set = buf.getFloat();
            uSlip = buf.getShort();
            uAlt_sonar = buf.getShort();
            uH_cmd = buf.getInt();
            uPhi_cmd = buf.getShort();
            uThe_cmd = buf.getShort();
            uPsi_cmd = buf.getShort();
            uPsi_path_cmd = buf.getShort();
            uV_cmd = buf.getShort();
            uAileron = buf.getShort();
            uElevator = buf.getShort();
            uEngine = buf.getShort();
        }

        @Override
        public byte[] SerializeBody() {
            return new byte[0];
        }

        @Override
        public byte getuMessageId() {
            return super.getuMessageId();
        }
    }

    public static class GCS_MISSION_INFO extends UVLinkMessage
    {
        private short uMissionID;
        private short uNext_WaypointID;
        private short uN_turn;
        private short uRel_psi_path;
        private int uD_err;
        private int uDist_to_go;
        private byte uMission_info0;
        private byte uMission_info1;
        private short uRsvd;

        /// <summary>
        /// id of mission engaged(negative if no mission is engaged)
        /// </summary>
        public short getMission_ID()
        {
            return uMissionID;
        }

        /// <summary>
        /// next waypoint id of current mission
        /// </summary>
        public short getNext_WaypointID()
        {
            return uNext_WaypointID;
        }

        /// <summary>
        /// number of turns(negative: straight line tracking, non-negative: no. of turns for circular path tracking)
        /// </summary>
        public short getN_turn()
        {
            return uN_turn;
        }

        /// <summary>
        /// relative path angle error (0.01deg)
        /// </summary>
        public float getRel_psi_path()
        {
            return (uRel_psi_path / 100.0f);
        }

        /// <summary>
        /// path error (cm)
        /// </summary>
        public float getD_err()
        {
            return (uD_err / 100.0f);
        }

        /// <summary>
        /// distance to go (cm)
        /// </summary>
        public float getDist_to_go()
        {
            return (uDist_to_go / 100.0f);
        }

        /// <summary>
        /// [0:3] swarm type(0: not swarm flight, 1:swarm path following, 2:swarm leader following)
        /// [4:7] swarm configuration(0: in-trail, 1:lateral, 2:oblique, 3:Triangle, 4: Diamond)
        /// </summary>
        public byte getMission_info0()
        {
            return uMission_info0;
        }
        public byte getSwarm_type()
        {
            return (byte)(uMission_info0 >> 4);
        }
        public byte getSwarm_config()
        {
            return (byte)((byte)(uMission_info0 << 4) >> 4);
        }

        /// <summary>
        /// [0:1] 0: not a swarm member, 1: a swarm member
        /// [2:3] 0: LTE, 1: Modem
        /// [4:7] 0: not involved,  1: approaching to swarm,  2: swarming
        /// </summary>
        public byte getMission_info1()
        {
            return uMission_info1;
        }
        public byte getSwarm_member()
        {
            return (byte)(uMission_info1 >> 6);

        }
        public byte getInter_comm_dev()
        {
            return (byte)((byte)(uMission_info1 << 2) >> 6);
        }
        public byte getSwarm_status()
        {
            return (byte)((byte)(uMission_info1 << 4) >> 4);
        }

        /// <summary>
        /// reserved for development
        /// </summary>
        public short getRsvd()
        {
            return uRsvd;
        }

        public GCS_MISSION_INFO()
        {
            uMessageId = 9;
        }

        public void DeserializeBody(byte[] s)
        {
            ByteBuffer buf = ByteBuffer.wrap(s);
            uMissionID = buf.getShort();
            uNext_WaypointID = buf.getShort();
            uN_turn = buf.getShort();
            uRel_psi_path = buf.getShort();
            uD_err = buf.getInt();
            uDist_to_go = buf.getInt();
            uMission_info0 = buf.get();
            uMission_info1 = buf.get();
            uRsvd = buf.getShort();
        }

        @Override
        public byte[] SerializeBody() {
            return new byte[0];
        }
    }

    public static class GCS_GPS_INFO extends UVLinkMessage
    {
        private int uH_sea_level;
        private int uH_wgs84;
        private short uWeek;
        private short uLeap_sec;
        private int uPbntime;
        private byte uNSV;
        private byte uType;
        private byte uSol_type;
        private byte uStatus;
        private short[] uPosStd = new short[3];
        private short[] uVelStd = new short[3];
        private short uPDOP;
        private short uHAcc;
        private short uVAcc;
        private byte uFlags;
        private byte uRsvd;

        /// <summary>
        /// altitude from sea level(from GPS) (cm)
        /// </summary>
        public float getH_sea_level()
        {
            return (uH_sea_level / 100.0f);
        }

        /// <summary>
        /// altitude from WGS84 ellipsoid (cm)
        /// </summary>
        public float getH_wgs84()
        {
            return (uH_wgs84 / 100.0f);
        }

        /// <summary>
        /// GPS week
        /// </summary>
        public short getWeek()
        {
            return uWeek;
        }

        /// <summary>
        /// leap second
        /// </summary>
        public short getLeap_sec()
        {
            return uLeap_sec;
        }

        /// <summary>
        /// GPS time (msec)
        /// </summary>
        public int getPbntime()
        {
            return uPbntime;
        }

        /// <summary>
        /// Number of satellite tracked
        /// </summary>
        public byte getNSV()
        {
            return uNSV;
        }

        /// <summary>
        /// GPS type, 0:Ublox, 1:Novatel
        /// </summary>
        public byte getType()
        {
            return uType;
        }

        /// <summary>
        /// FIX2D, FIX3D, TIMEONLYFIX
        /// </summary>
        public byte getSol_type()
        {
            return uSol_type;
        }

        /// <summary>
        /// GPS status(negative: wrong, otherwise: normal)
        /// </summary>
        public byte getStatus()
        {
            return uStatus;
        }

        /// <summary>
        /// Postion Standard deviation
        /// </summary>
        public short[] getPosStd()
        {
            return uPosStd;
        }

        /// <summary>
        /// Velocity Standard deviation
        /// </summary>
        public short[] getVelStd()
        {
            return uVelStd;
        }

        /// <summary>
        /// Position DOP
        /// </summary>
        public short getPDOP()
        {
            return uPDOP;
        }

        /// <summary>
        /// Vertical DOP
        /// </summary>
        public short getHAcc()
        {
            return uHAcc;
        }

        /// <summary>
        /// Horizontal DOP
        /// </summary>
        public short getVAcc()
        {
            return uVAcc;
        }

        /// <summary>
        /// Bitwize(0x01: GPSFixOK, 0x02: DiffSol, 0x04: WKNSET,  0x08, T0WSET
        /// </summary>
        public byte getFlags()
        {
            return uFlags;
        }

        /// <summary>
        /// reserved
        /// </summary>
        public byte getRsvd()
        {
            return uRsvd;
        }

        public GCS_GPS_INFO()
        {
            uMessageId = 11;
        }

        public void DeserializeBody(byte[] s)
        {
            ByteBuffer buf = ByteBuffer.wrap(s);
            uH_sea_level = buf.getInt();
            uH_wgs84 = buf.getInt();
            uWeek = buf.getShort();
            uLeap_sec = buf.getShort();
            uPbntime = buf.getInt();
            uNSV = buf.get();
            uType = buf.get();
            uSol_type = buf.get();
            uStatus = buf.get();
            uPosStd[0] = buf.getShort();
            uPosStd[1] = buf.getShort();
            uPosStd[2] = buf.getShort();
            uVelStd[0] = buf.getShort();
            uVelStd[1] = buf.getShort();
            uVelStd[2] = buf.getShort();
            uPDOP = buf.getShort();
            uHAcc = buf.getShort();
            byte[] bu = new byte[2];
            buf.get(bu);
            try {
                uVAcc = getBigEndian(bu);
            } catch (Exception e) {}
            //uVAcc = buf.getShort();
            uFlags = buf.get();
            uRsvd = buf.get();
        }

        @Override
        public byte[] SerializeBody() {
            return new byte[0];
        }

        @Override
        public byte getuMessageId() {
            return super.getuMessageId();
        }
    }

    public static class GCS_SYS_STATUS extends UVLinkMessage
    {
        private int uElapsed_time;
        private int uLog_start_time;
        private int uLog_available_time;
        private short uBatt_volt;
        private short uMot_volt;
        private short uMot_current;
        private short uModem_intensity;
        private byte uINS_gps_status;
        private byte uControl_mode;
        private byte uMode1;
        private byte uTime_zone;
        private byte uMode2;
        private byte uAltitude_type;
        private byte uVeh;
        private byte uMode3;
        private byte uAction;
        private byte uActionlog_id;
        private short uActionlog_page_left;

        /// <summary>
        /// elapsed time after the system started operation (msec)
        /// </summary>
        public int Elapsed_time()
        {
            return uElapsed_time;
        }

        /// <summary>
        /// elapsed time when current data log started (msec)
        /// </summary>
        public int Log_start_time()
        {
            return uLog_start_time;
        }

        /// <summary>
        /// time during which current log can be continued with flash memory (msec)
        /// </summary>
        public int Log_available_time()
        {
            return uLog_available_time;
        }

        /// <summary>
        /// FCC battery voltage (mV)
        /// </summary>
        public float Batt_volt()
        {
            return (uBatt_volt / 1000.0f);
            //get { return 12.1f; }
        }

        /// <summary>
        /// Motor batter voltage (mV)
        /// </summary>
        public float Mot_volt()
        {
            return (uMot_volt / 1000.0f);
            //get { return 24.1f; }
        }

        /// <summary>
        /// Current consumed by motor (mA)
        /// </summary>
        public float Mot_current()
        {
            return uMot_current;
        }

        /// <summary>
        /// Internal Modem Intensity (mV)
        /// </summary>
        public float Modem_intensity()
        {
            return (uModem_intensity);
        }

        /// <summary>
        /// INSGPS status(0: INSGPS/ 1:AHRS)
        /// </summary>
        public byte INS_gps_status()
        {
            return uINS_gps_status;
        }

        /// <summary>
        /// control mode: MANUAL_MODE, STICK_AUTO_MODE, AUTO_PILOT_BANK, AUTO_PILOT_TRACK,AUTO_PILOT_HEADING
        /// </summary>
        public byte Control_mode()
        {
            return uControl_mode;
        }

        /// <summary>
        /// [0] maintenance ( 0 - normal, 1-maintenance )
        /// [1] sas_on ( SAS On/Off )
        /// [2] steer_on (steering SAS on)
        /// [3:4] controller_type (0 - pid, 1 - neural net, 2 - dynamic inversion, 3 - other type for test)
        /// [5:7] altspd_mode (Alt/Speed Hold mode)
        /// </summary>
        public byte Mode1()
        {
            return uMode1;
        }
        public byte Maintenance()
        {
            return (byte)(uMode1 & 0x01);
        }
        public byte Sas_on()
        {
            return (byte)(uMode1 & 0x02);
        }
        public byte Steer_on()
        {
            return (byte)(uMode1 & 0x04);
        }
        public byte Controller_type()
        {
            return (byte)(uMode1 & 0x18);
        }
        public byte AltSpd_mode()
        {
            return (byte)(uMode1 & 0xE0);
        }

        /// <summary>
        /// Local UTC time zone(currently time zone for korea) (hour)
        /// </summary>
        public byte Time_zone()
        {
            return uTime_zone;
        }

        /// <summary>
        /// [0:1] 0: ground altimeter unlocked, 1: ground altimeter locked
        /// [2:3] control authority(0: GCS, 1: EP
        /// [4:5] FCC power warning flag(0 : normal, 1 : low battery)
        /// [6:7] Motor power waring flag(0 : normal, 1 : low battery)
        /// </summary>
        public byte Mode2()
        {
            return uMode2;
        }
        public byte Gnd_alt_lock()
        {
            return (byte)(uMode2 & 0x03);
        }
        public byte Control_authority()
        {
            return (byte)(uMode2 & 0x0C);
        }
        public byte Fcc_bat_warning()
        {
            return (byte)(uMode2 & 0x30);
        }
        public byte Mot_bat_warning()
        {
            return (byte)(uMode2 & 0xC0);
        }

        /// <summary>
        /// Altitude type used in autopilot(0: baro altitude, 1:WGS-84 GPS altitude)
        /// </summary>
        public byte Altitude_type()
        {
            return uAltitude_type;
        }

        /// <summary>
        /// [0:3] vehicle configuration(0:fixed wing, 1:multicopter, 2: combined, 3: reserved)
        /// [4:7] current vehicle mode(0: fixed wing, 1:multicopter, 2: fixed=>multicopter, 3: multicopter==>fixed)
        /// </summary>
        public byte Veh()
        {
            return uVeh;
        }
        public byte Veh_config()
        {
            return (byte)(uVeh & 0x0F);
        }
        public byte Cur_veh_config()
        {
            return (byte)(uVeh & 0xF0);
        }

        /// <summary>
        /// [0:5] multicopter controller mode
        /// [6:7] Low GPS accuracy(-1: GPS loss, 0 : normal, 1 : accuracy degraded)
        /// </summary>
        public byte Mode3()
        {
            return uMode3;
        }
        public byte Mlt_control_mode()
        {
            return (byte)(uMode3 & 0x3F);
        }
        public byte Gps_accuracy_warning()
        {
            return (byte)(uMode3 & 0xC0);
        }

        /// <summary>
        /// [6:7] 0 : stop logging, 1: under logging
        /// </summary>
        public byte Action()
        {
            return uAction;
        }
        public byte Action_status()
        {
            return (byte)(uAction & 0x3F);
        }
        public byte Actionlog_start()
        {
            return (byte)(uAction & 0xC0);
        }

        public byte Actionlog_id()
        {
            return uActionlog_id;
        }

        public short Actionlog_page_left()
        {
            return uActionlog_page_left;
        }


        public GCS_SYS_STATUS()
        {
            uMessageId = 12;
        }

        public void DeserializeBody(byte[] s)
        {
            try {
                getBigEndian(s);
            } catch (Exception e) {};
            ByteBuffer buf = ByteBuffer.wrap(s);
            uElapsed_time = buf.getInt();
            uLog_start_time = buf.getInt();
            uLog_available_time = buf.getInt();
            uBatt_volt = buf.getShort();
            uMot_volt = buf.getShort();
            uMot_current = buf.getShort();
            uModem_intensity = buf.getShort();
            uINS_gps_status = buf.get();
            uControl_mode = buf.get();
            uMode1 = buf.get();
            uTime_zone = buf.get();
            uMode2 = buf.get();
            uAltitude_type = buf.get();
            uVeh = buf.get();
            uMode3 = buf.get();
            uAction = buf.get();
            uActionlog_id = buf.get();
            uActionlog_page_left = buf.getShort();
        }

        @Override
        public byte[] SerializeBody() {
            return new byte[0];
        }

        @Override
        public byte getuMessageId() {
            return super.getuMessageId();
        }
    }
// ------------------------------------------------------------

    public static short getBigEndian(byte[] v)throws Exception{
        short[] arr = new short[2];
        for(int i=0;i<2;i++){
            arr[i] = (short)(v[1-i] & 0xFF);
        }
        return (short)((arr[0] << 8) + (arr[1] << 0));
    }



    public static UVLinkMessage CreateFromCommand(byte cmd)
    {
        switch ((int)cmd)
        {
            case 5: return new GCS_REPLY_ACK();
            case 6: return new GCS_REPLY_NACK();
            case 7: return new GCS_FLIGHT_INFO_1();
            case 8: return new GCS_FLIGHT_INFO_2();
            case 9: return new GCS_MISSION_INFO();
            case 11: return new GCS_GPS_INFO();
            case 12: return new GCS_SYS_STATUS();
            /*case 20: return new GCS_HOME_POS();
            case 89: return new GCS_MODEM_INFO();
            case 147: return new GCS_CUR_AUTO_CMD();
            case 159: return new GCS_EMERGENCY_INFO();
            case 160: return new GCS_MISSION_BLOCK_INFO();
            case 161: return new GCS_MISSION_ATTR();
            case 162: return new GCS_MISSION_WP();
            case 163: return new GCS_LANDING_MISSION_ATTR();
            case 164: return new GCS_LANDING_MISSION_WP();
            case 165: return new GCS_ONLINE_MISSION_ATTR();
            case 166: return new GCS_ONLINE_MISSION_WP();
            case 167: return new GCS_MISSION_LOITER();
            case 168: return new GCS_MISSION_SCAN();
            case 170: return new GCS_MISSION_FOR_TAKEOFF();
            case 255: return new GCS_SYS_FAULT();*/
            default: return null;
        }
    }
}
