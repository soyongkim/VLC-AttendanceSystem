package com.hoho.android.usbserial.util;

import java.nio.ByteBuffer;

/**
 * Created by user on 2017-11-29.
 */
public class UVLinkPacket {
    public final int PacketHeaderNumBytes = 6;
    public final int PacketTailNumBytes = 2;
    public boolean IsValid = false;
    public static byte syncCheck;
    public UVLinkMessage Message;

    //Header
    public byte Sync1;                      //Up/Down link synchronous byte #1
    public byte Sync2;                      //Up/Down link synchronous byte #2
    public byte VehicleID;                  //vehicle id specified by user for future use (default 1)
    public byte PayLoadLength;              //byte size of packet data(should be multiple of 4)
    public byte Command;                    //Up/Down link command
    public byte PacketCount;                //packet count

    //Body
    public byte[] Payload;                  //Up/Down link Data (less than 56 byte)

    //Tail
    public byte CheckSum1;                  //CRC Code (VehicleID ~ Data)
    public byte CheckSum2;

    final short poly = 4129;
    static short[] crc16_ccitt_table = new short[256];

    public int crc_available = 0;

    public UVLinkPacket()
    {
        short temp, a;
        for (int i = 0; i < crc16_ccitt_table.length; ++i)
        {
            temp = 0;
            a = (short)(i << 8);
            for (int j = 0; j < 8; ++j)
            {
                if (((temp ^ a) & 0x8000) != 0)
                {
                    temp = (short)((temp << 1) ^ poly);
                }
                else
                {
                    temp <<= 1;
                }
                a <<= 1;
            }
            crc16_ccitt_table[i] = temp;
        }
    }

    public static byte[] GetBytesForMessage(UVLinkMessage msg, byte veh_id, byte count)
    {
            UVLinkPacket p = UVLinkPacket.GetPacketForMessage(msg, veh_id, count);

            int bufferSize = p.GetPacketSize();

            byte[] result = new byte[bufferSize];
            result = p.Serialize(result, p);


        return result;


    }

    public static UVLinkPacket GetPacketForMessage(UVLinkMessage msg, byte veh_id, byte count)
    {
        UVLinkPacket result = new UVLinkPacket();
        result.Sync1 = 'U';
        result.Sync2 = 'p';
        result.VehicleID = veh_id;
        result.PacketCount = count;
        result.Command = msg.uMessageId;
        result.Payload = msg.SerializeBody();
        result.PayLoadLength = 0;
        result.UpdateCrc();
        return result;
    }

    private void UpdateCrc()
    {
        short crc = GetPacketCrc(this);
        CheckSum1 = (byte)(crc & 0xFF);
        CheckSum2 = (byte)(crc >> 8);
    }


    public int GetPacketSize()
    {
        return PacketHeaderNumBytes + PayLoadLength + PacketTailNumBytes;
    }

    public byte[] Serialize(byte[] w, UVLinkPacket p)
    {
        ByteBuffer bw = ByteBuffer.wrap(w);
        //header
        bw.put(p.Sync1);
        bw.put(p.Sync2);
        bw.put(p.VehicleID);
        bw.put(p.PayLoadLength);
        bw.put(p.Command);
        bw.put(p.PacketCount);
        //body
        bw.put(p.Payload);
        //tail
        bw.put(p.CheckSum1);
        bw.put(p.CheckSum2);

        return bw.array();
    }

    public static short GetPacketCrc(UVLinkPacket p)
    {
        short headerByte1, headerByte2;
        short crc  = 0;
        headerByte1 = (short)(p.PayLoadLength << 8);
        headerByte1 ^= p.VehicleID;

        headerByte2 = (short)(p.PacketCount << 8);
        headerByte2 ^= p.Command;

        crc = crc16_ccitt(headerByte1, crc);
        crc = crc16_ccitt(headerByte2, crc);
        int i;
        try
        {
           for (i = 0; i < p.Payload.length;)
            {
                short payloadByte;

                payloadByte = (short)(p.Payload[i + 1] << 8);
                payloadByte ^= p.Payload[i];

                crc = crc16_ccitt(payloadByte, crc);

                i += 2;
            }

        }
        catch (Exception ex) {  }

        return crc;
    }

    public static short crc16_ccitt(short buf, short crc)
    {

        crc = (short)((crc << 8) ^ crc16_ccitt_table[((crc >> 8) ^ (buf & 0x00FF)) & 0x00FF]);
        crc = (short)((crc << 8) ^ crc16_ccitt_table[((crc >> 8) ^ (buf >> 8)) & 0x00FF]);

        return crc;
    }

    private boolean IsValidCrc()
    {
        short crc = GetPacketCrc(this);

        return (((byte)(crc & 0xFF) == CheckSum1) &&
                ((byte)(crc >> 8) == CheckSum2));
    }



    public static UVLinkPacket Deserialize(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data);

        //header
        UVLinkPacket result = new UVLinkPacket();
        result.Sync1 = 0x44;
        result.Sync2 = buf.get();
        result.VehicleID = buf.get();
        result.PayLoadLength = buf.get();
        result.Command = buf.get();
        result.PacketCount = buf.get();

        //body
        result.Payload = new byte[result.PayLoadLength];
        buf.get(result.Payload);

        //tail
        result.CheckSum1 = buf.get();
        result.CheckSum2 = buf.get();

       if (result.IsValidCrc()) {
            result.DeserializeMessage();
            result.crc_available = 1;
        }


        //result.DeserializeMessage();

        return result;
    }


    private void DeserializeMessage()
    {
        UVLinkMessage result = GeneratedMessages.CreateFromCommand(Command);

        if (result == null) return;  // Unknown type

        result.DeserializeBody(Payload);

        Message = result;
        IsValid = true;
    }

}
