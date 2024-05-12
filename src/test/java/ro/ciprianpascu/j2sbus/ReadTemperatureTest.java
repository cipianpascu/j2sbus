package ro.ciprianpascu.j2sbus;

import static org.junit.Assert.fail;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Enumeration;

import org.junit.Before;
import org.junit.Test;

public class ReadTemperatureTest {
	
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	/* CRCtable */
	private static final int[] mbufintCRCTable ={
		0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7,
		0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef,
		0x1231, 0x0210, 0x3273, 0x2252, 0x52b5, 0x4294, 0x72f7, 0x62d6,
		0x9339, 0x8318, 0xb37b, 0xa35a, 0xd3bd, 0xc39c, 0xf3ff, 0xe3de,
		0x2462, 0x3443, 0x0420, 0x1401, 0x64e6, 0x74c7, 0x44a4, 0x5485,
		0xa56a, 0xb54b, 0x8528, 0x9509, 0xe5ee, 0xf5cf, 0xc5ac, 0xd58d,
		0x3653, 0x2672, 0x1611, 0x0630, 0x76d7, 0x66f6, 0x5695, 0x46b4,
		0xb75b, 0xa77a, 0x9719, 0x8738, 0xf7df, 0xe7fe, 0xd79d, 0xc7bc,
		0x48c4, 0x58e5, 0x6886, 0x78a7, 0x0840, 0x1861, 0x2802, 0x3823,
		0xc9cc, 0xd9ed, 0xe98e, 0xf9af, 0x8948, 0x9969, 0xa90a, 0xb92b,
		0x5af5, 0x4ad4, 0x7ab7, 0x6a96, 0x1a71, 0x0a50, 0x3a33, 0x2a12,
		0xdbfd, 0xcbdc, 0xfbbf, 0xeb9e, 0x9b79, 0x8b58, 0xbb3b, 0xab1a,
		0x6ca6, 0x7c87, 0x4ce4, 0x5cc5, 0x2c22, 0x3c03, 0x0c60, 0x1c41,
		0xedae, 0xfd8f, 0xcdec, 0xddcd, 0xad2a, 0xbd0b, 0x8d68, 0x9d49,
		0x7e97, 0x6eb6, 0x5ed5, 0x4ef4, 0x3e13, 0x2e32, 0x1e51, 0x0e70,
		0xff9f, 0xefbe, 0xdfdd, 0xcffc, 0xbf1b, 0xaf3a, 0x9f59, 0x8f78,
		0x9188, 0x81a9, 0xb1ca, 0xa1eb, 0xd10c, 0xc12d, 0xf14e, 0xe16f,
		0x1080, 0x00a1, 0x30c2, 0x20e3, 0x5004, 0x4025, 0x7046, 0x6067,
		0x83b9, 0x9398, 0xa3fb, 0xb3da, 0xc33d, 0xd31c, 0xe37f, 0xf35e,
		0x02b1, 0x1290, 0x22f3, 0x32d2, 0x4235, 0x5214, 0x6277, 0x7256,
		0xb5ea, 0xa5cb, 0x95a8, 0x8589, 0xf56e, 0xe54f, 0xd52c, 0xc50d,
		0x34e2, 0x24c3, 0x14a0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
		0xa7db, 0xb7fa, 0x8799, 0x97b8, 0xe75f, 0xf77e, 0xc71d, 0xd73c,
		0x26d3, 0x36f2, 0x0691, 0x16b0, 0x6657, 0x7676, 0x4615, 0x5634,
		0xd94c, 0xc96d, 0xf90e, 0xe92f, 0x99c8, 0x89e9, 0xb98a, 0xa9ab,
		0x5844, 0x4865, 0x7806, 0x6827, 0x18c0, 0x08e1, 0x3882, 0x28a3,
		0xcb7d, 0xdb5c, 0xeb3f, 0xfb1e, 0x8bf9, 0x9bd8, 0xabbb, 0xbb9a,
		0x4a75, 0x5a54, 0x6a37, 0x7a16, 0x0af1, 0x1ad0, 0x2ab3, 0x3a92,
		0xfd2e, 0xed0f, 0xdd6c, 0xcd4d, 0xbdaa, 0xad8b, 0x9de8, 0x8dc9,
		0x7c26, 0x6c07, 0x5c64, 0x4c45, 0x3ca2, 0x2c83, 0x1ce0, 0x0cc1,
		0xef1f, 0xff3e, 0xcf5d, 0xdf7c, 0xaf9b, 0xbfba, 0x8fd9, 0x9ff8,
		0x6e17, 0x7e36, 0x4e55, 0x5e74, 0x2e93, 0x3eb2, 0x0ed1, 0x1ef0
	};

	//CONST 
	private static final int CONST_UDP_UDP_PORT=6000;
	private static final int CONST_MAX_UPD_PACKET_LEN=1024;
	private static final int CONST_MAX_TIMES_OF_SEND=2;
	private static final int CONST_TIME_OUT_FOR_TOTAL_WAIT=4000; //millisecond
	private static final int CONST_TIME_OUT_FOR_EACH_WAIT=1000; //millisecond

	//position of udp buffer
	public static final byte CONST_START_PST_OF_ADDITIONAL_DATA_IN_FULL_PACKETS=25;
	public static final byte CONST_START_PST_OF_ADDITIONAL_DATA_WITHOUT_AA_PACKETS=9;
	public static final byte CONST_START_PST_OF_LEN_OF_DATA_IN_FULL_PACKETS=16;

	private static final short CONST_SELF_SUBNET_ID=(short) 0x01;
	private static final short CONST_SELF_DEVICE_ID=(short) 0xFE;
	private static final short CONST_SELF_DEVICE_TYPE_H=(short) 0xFE;
	private static final short CONST_SELF_DEVICE_TYPE_L=(short) 0xFE;
	
	DatagramSocket udpSocket;
	@Before
	public void before() throws SocketException {
		udpSocket = new DatagramSocket(null);
		udpSocket.setReuseAddress(true);
		udpSocket.setBroadcast(true);
		udpSocket.bind(new InetSocketAddress(CONST_UDP_UDP_PORT));
	}

	@Test
	public void test() {
		byte[] result = ReadACTempType((byte)1, (byte)61);
		System.out.println(bytesToHex(result, result.length));
	}
	
    //Air condition begin
    public byte[]  ReadACTempType(byte byteSubnetID,byte byteDeviceID)
    {
    	boolean blnSuccess=false;
    	byte[] arraybyteBufWithoutAA=null;
    	
    	try
    	{        	
    		int intOP=0xE120;
       		short shortLenOfAddtionalBuf;
       		
    		byte[] arrayAddtional =new byte[0];
    		shortLenOfAddtionalBuf=(short) (arrayAddtional.length);
    		blnSuccess=SendUDPBuffer(arrayAddtional, shortLenOfAddtionalBuf, intOP, byteSubnetID, byteDeviceID, false);
     	    if (blnSuccess==true)	
     	    {
     	    	arraybyteBufWithoutAA=UDPReceive(byteSubnetID, byteDeviceID, intOP,true);
     	    }
    		
    		
    		
    	}catch(Exception e)
    	{ 
    		// Toast.makeText(getApplicationContext(), e.getMessage(),
	  		       //   Toast.LENGTH_SHORT).show();	
    	}
    	return arraybyteBufWithoutAA;
    }
    
	/*
	 * Send buffer by UDP Socket
	*/
	public boolean SendUDPBuffer(byte[] arrayAddtional,short shortLenOfAddtionalBuf,int intOP, byte byteObjSubnetID,byte byteObjDeviceID,boolean blnBigPack)
	{
		boolean blnSuccess =false;
		boolean blnNeedShowIPError=false;
		String strLocalIP;
		short shortLenOfBaseData,shortI,shortLenOfPackCRCBufWithAA,shortLenOfPackCRCBufWithoutAA,shortLenOfSend;
		DatagramPacket oDataPacket;
		byte[] bytebufSend;
		byte[] arraybyteLocalIP=new byte[4];
		byte[] arraybyteTargetIP=new byte[4];
		//byte[] arraybyteBufRec = new byte[CONST_MAX_UPD_PACKET_LEN];
		//byte[] arraybyteRec;
		
		//DatagramPacket oPacket = new DatagramPacket(arraybyteBufRec,arraybyteBufRec.length);
									
		try
		{
			//bytebufRec=new byte[300];       
			
			arraybyteLocalIP=GetLocalIP();
						
			if (arraybyteLocalIP  != null)
			{
			     
			}
			else
			{
				blnNeedShowIPError=true;
				
				
				blnSuccess=false;
				return blnSuccess;
				
			};
						
			arraybyteTargetIP=GetTargetIP(arraybyteLocalIP);
			
			shortLenOfBaseData=11;
			shortLenOfPackCRCBufWithoutAA=(short) (shortLenOfBaseData+shortLenOfAddtionalBuf);
			shortLenOfPackCRCBufWithAA=(short) (shortLenOfPackCRCBufWithoutAA+2);
			shortLenOfSend=(short) (shortLenOfPackCRCBufWithAA+14);
			bytebufSend=new byte[shortLenOfSend];
			byte [] arrayPackCRC=new byte[shortLenOfPackCRCBufWithoutAA];
			bytebufSend[0]=arraybyteLocalIP[0];
			bytebufSend[1]=arraybyteLocalIP[1];
			bytebufSend[2]=arraybyteLocalIP[2];
			bytebufSend[3]=arraybyteLocalIP[3];
				
			bytebufSend[4]=0x53; //S
			bytebufSend[5]=0x4D; //M
			bytebufSend[6]=0x41; //A
			bytebufSend[7]=0x52; //R
			bytebufSend[8]=0x54; //T
			bytebufSend[9]=0x43; //C
			bytebufSend[10]=0x4C; //L
			bytebufSend[11]=0x4F; //O
			bytebufSend[12]=0x55; //U
			bytebufSend[13]=0x44; //D
		
			bytebufSend[14]=(byte) 0xAA; //
			bytebufSend[15]=(byte) 0xAA; //
            
        	//data size
		   	if ((blnBigPack==true) || ((shortLenOfAddtionalBuf+shortLenOfBaseData)>80)) 
        	{
        		arrayPackCRC[0]=(byte) 0xFF;
            }
            else
            {
            	arrayPackCRC[0]=(byte) shortLenOfPackCRCBufWithoutAA; 
            };
         
            arrayPackCRC[0]=(byte) shortLenOfPackCRCBufWithoutAA; 
            arrayPackCRC[1]=(byte) CONST_SELF_SUBNET_ID; //
            arrayPackCRC[2]=(byte) CONST_SELF_DEVICE_ID; //
            arrayPackCRC[3]=(byte) CONST_SELF_DEVICE_TYPE_H; //
            arrayPackCRC[4]=(byte) CONST_SELF_DEVICE_TYPE_L; //
            arrayPackCRC[5]=(byte) (intOP/256); //H bit of operation code 
            arrayPackCRC[6]=(byte) (intOP%256); //L bit of operation code
            arrayPackCRC[7]=byteObjSubnetID; //
            arrayPackCRC[8]=byteObjDeviceID; //
            if (shortLenOfAddtionalBuf>0)
            {
            	for(shortI=0;shortI<=shortLenOfAddtionalBuf-1;shortI++)
            	{
            		arrayPackCRC[9+shortI]=arrayAddtional[shortI];
            	}
            }
        	                
        	if (blnBigPack==false) 
        	{
           	  PackCRC(arrayPackCRC,(short) (arrayPackCRC.length-2));	
        	}
        	        	
        	for(shortI=0;shortI<=arrayPackCRC.length-1;shortI++)
        	{
        		bytebufSend[shortI+16]=arrayPackCRC[shortI];
        	}
          
        	oDataPacket = new DatagramPacket(bytebufSend, shortLenOfSend,
        	     InetAddress.getByAddress(arraybyteTargetIP), CONST_UDP_UDP_PORT);
        	
        	
        	udpSocket.send(oDataPacket);
           	blnSuccess=true;
             	
   		
		}catch(Exception e)
		{
			//Toast.makeText(getApplicationContext(), e.getMessage(),
	  		          //Toast.LENGTH_SHORT).show();

		}
		return blnSuccess;
	}
	
    public byte[] UDPReceive(byte byteSubnetID,byte byteDeviceID,int intOP,boolean blnNeedCheckAddressOfFeedback)
    {
    	boolean blnSuccess=false;
       	byte[] arraybyteBufRec=null;
    	byte[] arraybyteBufWithoutAA=null;
    	    	
    	try
    	{
    		boolean blnContinute;
    		long lngStartTime_of_MS;
        	long lngCurTime_of_MS;
        	
        	byte byteSrcSubnetID_of_reply;
        	byte byteSrcDeviceID_of_reply;
        	int  intOP_of_reply;
        	int intOP_H,intOP_L;
        	int intTimes=0;
        	Calendar oCal;
     
        	oCal=Calendar.getInstance();  
    		lngStartTime_of_MS=oCal.getTimeInMillis();  
    		lngCurTime_of_MS=lngStartTime_of_MS;
    		while((lngCurTime_of_MS-lngStartTime_of_MS)<=CONST_TIME_OUT_FOR_TOTAL_WAIT)
     		{
    			try
        		{
   				
    				if (IsSocketClose()==true) 
        			{
        				return null;
        			}
    	
    			     				
    				intTimes=intTimes+1;
    				byte[] arraybyteBufTEMP= new byte[CONST_MAX_UPD_PACKET_LEN];
    				DatagramPacket oPacketRec = new DatagramPacket(arraybyteBufTEMP,arraybyteBufTEMP.length);
    		    	
    				udpSocket.setSoTimeout(CONST_TIME_OUT_FOR_EACH_WAIT);
    				udpSocket.setReceiveBufferSize(CONST_MAX_UPD_PACKET_LEN);
    				udpSocket.receive(oPacketRec);
      				arraybyteBufRec=oPacketRec.getData();
					
      				intOP_H=(arraybyteBufRec[CONST_START_PST_OF_LEN_OF_DATA_IN_FULL_PACKETS+5] *256) & 0xFFFF;
				    intOP_L=arraybyteBufRec[CONST_START_PST_OF_LEN_OF_DATA_IN_FULL_PACKETS+6] & 0xFF;
					intOP_of_reply=intOP_H+intOP_L;
					
					if ((intOP_of_reply==(intOP+1)))
					{
						if (blnNeedCheckAddressOfFeedback==true)
						{
							byteSrcSubnetID_of_reply=arraybyteBufRec[CONST_START_PST_OF_LEN_OF_DATA_IN_FULL_PACKETS+1];
							byteSrcDeviceID_of_reply=arraybyteBufRec[CONST_START_PST_OF_LEN_OF_DATA_IN_FULL_PACKETS+2];   
							if ((byteSrcSubnetID_of_reply==byteSubnetID) & (byteSrcDeviceID_of_reply==byteDeviceID) )
							{
								blnContinute=true;
							}
							else
							{
								blnContinute=false;
							}
						}
						else
						{
							blnContinute=true;
						}
						
						if (blnContinute==true)
						{
							arraybyteBufWithoutAA=ProcessUDPPackets(arraybyteBufRec);
		    				if (arraybyteBufWithoutAA!=null)
		        			{
		    					blnSuccess=true;
		    					break;
		       				}
						}
						
   				    }

					lngCurTime_of_MS=System.currentTimeMillis();
    				if ((lngCurTime_of_MS-lngStartTime_of_MS)>CONST_TIME_OUT_FOR_TOTAL_WAIT )
    				{
    					break;
    				}
    				
    		
    				
        		}catch(Exception e)
    			{
       				lngCurTime_of_MS=System.currentTimeMillis();
    				if ((lngCurTime_of_MS-lngStartTime_of_MS)>CONST_TIME_OUT_FOR_TOTAL_WAIT )
    				{
    					break;
    				}
    		
    			}
        		
    		}
    	    //receive packets end
    		
    		
    	}catch(Exception e)
    	{ 
    		// Toast.makeText(getApplicationContext(), e.getMessage(),
	  		          //Toast.LENGTH_SHORT).show();	
    	}
    	finally
    	{
    
    	}
    	
    	if (blnSuccess==true)
    	{
    	    return arraybyteBufWithoutAA;
    	    
    	}
		else
		{
			return null;
		}
    }
    
    public byte[] ProcessUDPPackets(byte[] arraybyteRec)
	{
    	byte[] arraybyteBufWithoutHead=null;
    	int intLenOfPackets;
    	boolean blnIsBigPack=false;
    	
		try
		{
			if (IsSocketClose()==true) 
			{
				return null;
			}
			
			int intSizeWithoutHead=0,intI=0;
			boolean blnNeedToCheckCRC=false;
					
			intLenOfPackets=arraybyteRec.length;
		    if ((intLenOfPackets<=0) || (intLenOfPackets<27) )
	        {
	           return null;
	        }
	           
		
			if (((arraybyteRec[14] & 0xFF)==0xAA) && (((arraybyteRec[15] & 0xFF)==0xAA) || ((arraybyteRec[15] & 0xFF)==0x55)))
			{
				//do nothing
			}
			else
			{
				return null;
			}
				
			if ((arraybyteRec[16] & 0xFF)==0xFF)
			{
				blnIsBigPack=true;
			}
			else
			{
				blnIsBigPack=false;
			}

			intSizeWithoutHead=intLenOfPackets-16;
			arraybyteBufWithoutHead=new byte[intSizeWithoutHead];
			for(intI=0;intI<arraybyteBufWithoutHead.length;intI++)
			{
				arraybyteBufWithoutHead[intI]=arraybyteRec[intI+16];
			}
			
			if (blnIsBigPack==true)
			{
				blnNeedToCheckCRC=false;
			}
			else
		    {
				blnNeedToCheckCRC=true;
				
		    };
		    
			if (IsSocketClose()==true) 
			{
				return null;
			}
			
		    if (blnNeedToCheckCRC==true)
		    {
		    	if (CheckCRC(arraybyteBufWithoutHead,arraybyteBufWithoutHead.length-2)==false)
		    	{
		       		arraybyteBufWithoutHead=null;
		    	}
		    };
				    
					
		}catch(Exception e)
		{
			//Toast.makeText(getApplicationContext(), e.getMessage(),
	  		      //    Toast.LENGTH_SHORT).show();	
		}
		
		return arraybyteBufWithoutHead;
	}
    
    
	public boolean IsSocketClose()
	{
		boolean blnIsClose=false;
		try
		{
			if (udpSocket==null)
			{
				blnIsClose=true;
			}
			else
			{
				if (udpSocket.isClosed()==true)
				{
					blnIsClose=true;
				}
			}
			
		}catch(Exception e)
		{
			
		}
		return blnIsClose;
	}
	
	/*
	 * Get 2 CRC bytes 
	 */
	protected void PackCRC(byte[] arrayBuf,short shortLenOfBuf)
	{
   	    short shortCRC=0;
   	    byte bytTMP=0;
   	    short shortIndexOfBuf=0;
   	    byte byteIndex_Of_CRCTable=0;
		while (shortLenOfBuf!=0) 
		{
			bytTMP= (byte) (shortCRC >> 8) ;    //>>: right move bit                              
			shortCRC=(short) (shortCRC << 8);   //<<: left  move bit   
			byteIndex_Of_CRCTable=(byte) (bytTMP ^ arrayBuf[shortIndexOfBuf]);
			shortCRC=(short) (shortCRC ^ mbufintCRCTable[(byteIndex_Of_CRCTable & 0xFF)]);   //^: xor
			shortIndexOfBuf=(short) (shortIndexOfBuf+1);
		    shortLenOfBuf=(short) (shortLenOfBuf-1);
		};
		
		arrayBuf[shortIndexOfBuf]=(byte) (shortCRC >> 8);
		shortIndexOfBuf=(short) (shortIndexOfBuf+1);
		arrayBuf[shortIndexOfBuf]=(byte) (shortCRC & 0x00FF);
			
	}
	
	/*
	 * Check the UDP packets is correct or not by checking CRC 
	 */
	public boolean CheckCRC(byte[] arrayBuf,int intlength)
	{
		boolean blnIsCorrenct=false;
		
		try
		{
			short shortCRC=0;
	   	    byte bytTMP=0;
	   	    short shortIndexOfBuf=0;
	   	    byte byteIndex_Of_CRCTable=0;
	        		
			if (IsSocketClose()==true) 
			{
				return false;
			}
			
			while (intlength!=0) 
			{
				bytTMP= (byte) (shortCRC >> 8) ;    //>>: right move bit                              
				shortCRC=(short) (shortCRC << 8);   //<<: left  move bit   
				byteIndex_Of_CRCTable=(byte) (bytTMP ^ arrayBuf[shortIndexOfBuf]);
				shortCRC=(short) (shortCRC ^ mbufintCRCTable[(byteIndex_Of_CRCTable & 0xFF)]);   //^: xor
				shortIndexOfBuf=(short) (shortIndexOfBuf+1);
			    intlength=(short) (intlength-1);
			};
			
			if (arrayBuf[shortIndexOfBuf]==(shortCRC >> 8) && arrayBuf[shortIndexOfBuf+1]==(short)(shortCRC & 0xFF))
		    { 
			    blnIsCorrenct=true;
		    }
		    else
		    {
		    	blnIsCorrenct=false;
		    };
		    
		}catch(Exception e)
		{
			//Toast.makeText(getApplicationContext(), e.getMessage(),
	  		     //     Toast.LENGTH_SHORT).show();	
		}
		    
	    return blnIsCorrenct;
		
	}
	
	
	public byte[] GetLocalIP()
	{	
		byte[] ipAddr = new byte[4];
		    
		try 
		{ 
			/*
		    InetAddress addr = InetAddress.getLocalHost(); 
	        ipAddr = addr.getAddress(); 
	        strIP=ipAddr[0]+"." +ipAddr[1]+"."+ipAddr[2]+"."+ipAddr[3];
	      	Log.d(CONST_CLASS_NAME, "Get Local IP"+strIP);
	      	
	      	//for testing
	      	/*ipAddr[0]=(byte) 192;
	      	ipAddr[1]=(byte) 168;
	      	ipAddr[2]=0;
	      	ipAddr[3]=(byte) 198;
	      	*/
			
			ipAddr=null;
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
			{
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
	            {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) 
	                {
	                	ipAddr= inetAddress.getAddress();
	                	return ipAddr;
	                }
	            }
	        }
		    return ipAddr;
		      
		    
		}
	    catch (Exception e) 
		{ 
	    	//Toast.makeText(getApplicationContext(), e.getMessage(),
	  		         // Toast.LENGTH_SHORT).show();	
		}
		return ipAddr;
		
	}

	/*
	 * 
	 */
	public byte[] GetTargetIP(byte[] arraybyteLocalIP)
	{	
		byte[] arraybyteTargetIP= new byte[4];
		byte byteBit;
	       
		try 
		{ 	
			byteBit=(byte) ((arraybyteLocalIP[0] & 0xFF)>>5);
			if (((byteBit & 0xFF)>=0) && ((byteBit & 0xFF)<=3)) //IP type:A
			{
				arraybyteTargetIP[0]=arraybyteLocalIP[0];
				arraybyteTargetIP[1]=(byte) 255;
				arraybyteTargetIP[2]=(byte) 255;
				arraybyteTargetIP[3]=(byte) 255;	
			}
			else if (((byteBit & 0xFF)>=4) && ((byteBit & 0xFF)<=5)) //IP Type:B
			{
				arraybyteTargetIP[0]=arraybyteLocalIP[0];
				arraybyteTargetIP[1]=arraybyteLocalIP[1];
				arraybyteTargetIP[2]=(byte) 255;
				arraybyteTargetIP[3]=(byte) 255;	
			}
			else if (((byteBit & 0xFF)>=6) && ((byteBit & 0xFF)<=7)) //IP Type:C
			{
				arraybyteTargetIP[0]=arraybyteLocalIP[0];
				arraybyteTargetIP[1]=arraybyteLocalIP[1];
				arraybyteTargetIP[2]=arraybyteLocalIP[2];
				arraybyteTargetIP[3]=(byte) 255;	
			}
			else
			{
				arraybyteTargetIP[0]=(byte) 255;
				arraybyteTargetIP[1]=(byte) 255;
				arraybyteTargetIP[2]=(byte) 255;
        		arraybyteTargetIP[3]=(byte) 255;	
			}
				
   	      
		    
		}
	    catch (Exception e) 
		{ 
			 //Toast.makeText(getApplicationContext(), e.getMessage(),
	  		         // Toast.LENGTH_SHORT).show();	
		}
	 
	   
		return arraybyteTargetIP;
		
	}
	
    public static String bytesToHex(byte[] bytes, int length) {
        char[] hexChars = new char[length * 2];
        for (int j = 0; j < length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
