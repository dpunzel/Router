import java.util.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author CSIS350 Group
 * David Punzel, Gohar Robert, Nicholas Jensen
 * Class Router - adds, deletes, and lookup
 * of IP address and route. Uses CIDR notation
 * to get IP ranges and adds to ArrayList for
 * retrieval.
 */
public class Router
{
   private String _routerRoute;
   private String _cidrNotation;
   private String _ipAddress;
   private String _mask;
   private String _cidrAndroute;
   private String _operationCmd;
   private InetAddress inetAddress;
   private InetAddress startAddress;
   private InetAddress endAddress;
   private int prefixLength;
   private int _route;
   public ArrayList<String[]> storage = new ArrayList<>();
   String sIP;
   String eIP;

   /**
    * Constructor router
    */
   public Router() {

   }

   /**
    *
    * @param cmd
    * @throws UnknownHostException
    */
   private void commandSplit(String cmd) throws UnknownHostException {
      String[] tempSplit = cmd.split("\\s");
      String[] ipSplitTemp = tempSplit[1].split("=");
      _operationCmd = tempSplit[0];
      _routerRoute = tempSplit[2].substring(tempSplit[2].lastIndexOf("=")+1);
      if (_routerRoute.contains("NULL")) {
         _routerRoute = "0";
      }
      _cidrNotation = ipSplitTemp[1].substring(0, ipSplitTemp[1].length()-1);
      _mask = _cidrNotation.substring(_cidrNotation.indexOf("/")+1);
      _ipAddress = _cidrNotation.substring(0, _cidrNotation.indexOf("/"));
      _cidrAndroute = tempSplit[1]+tempSplit[2];
      inetAddress = InetAddress.getByName(_ipAddress);
      prefixLength = Integer.parseInt(_mask);
   }

   private String lookUpSplit(String cmd) {
      String[] lookUpSplit = cmd.split("=");
      return lookUpSplit[1];
   }

   /**
    * Helper method for ParseCmd
    * @return Network Address
    */
   private String getNetworkAddress() {

      return this.startAddress.getHostAddress();
   }

   /**
    *
    * @return
    */
   private String getBroadcastAddress() {
      return this.endAddress.getHostAddress();
   }

   private void calculate() throws UnknownHostException {

      ByteBuffer maskBuffer;
      int targetSize;
      if (inetAddress.getAddress().length == 4) {
         maskBuffer =
                 ByteBuffer
                         .allocate(4)
                         .putInt(-1);
         targetSize = 4;
      } else {
         maskBuffer = ByteBuffer.allocate(16)
                 .putLong(-1L)
                 .putLong(-1L);
         targetSize = 16;
      }

      BigInteger mask = (new BigInteger(1, maskBuffer.array())).not().shiftRight(prefixLength);

      ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
      BigInteger ipVal = new BigInteger(1, buffer.array());

      BigInteger startIp = ipVal.and(mask);
      BigInteger endIp = startIp.add(mask.not());

      byte[] startIpArr = toBytes(startIp.toByteArray(), targetSize);
      byte[] endIpArr = toBytes(endIp.toByteArray(), targetSize);

      this.startAddress = InetAddress.getByAddress(startIpArr);
      this.endAddress = InetAddress.getByAddress(endIpArr);

      sIP = String.valueOf(startAddress).substring(1,String.valueOf(startAddress).length());
      eIP = String.valueOf(endAddress).substring(1,String.valueOf(endAddress).length());

   }

   private byte[] toBytes(byte[] array, int targetSize) {
      int counter = 0;
      List<Byte> octectCalcArray = new ArrayList<Byte>();
      while (counter < targetSize && (array.length - 1 - counter >= 0)) {
         octectCalcArray.add(0, array[array.length - 1 - counter]);
         counter++;
      }

      int size = octectCalcArray.size();
      for (int i = 0; i < (targetSize - size); i++) {

         octectCalcArray.add(0, (byte) 0);
      }

      byte[] byteReturnOctect = new byte[octectCalcArray.size()];
      for (int i = 0; i < octectCalcArray.size(); i++) {
         byteReturnOctect[i] = octectCalcArray.get(i);
      }
      return byteReturnOctect;
   }

   /**
    *
    * @param startIP
    * @param endIP
    * @param mask
    * @param route
    * @param cidr
    * @return
    */
   private ArrayList<String[]> addIpRangesWithMaskRoute(String startIP, String endIP, String mask, String route, String cidr) {
      String[] IPAddress = new String[5];
      IPAddress[0] = startIP;
      IPAddress[1] = endIP;
      IPAddress[2] = mask;
      IPAddress[3] = route;
      IPAddress[4] = cidr;

      storage.add(IPAddress);

      return storage;
   }

   /**
    *
    * @param ip
    * @return
    */
   private String convertIpToDecimal(String ip) {
      String ipAddress = ip;
      String[] addrArray = ipAddress.split("\\.");

      long ipDecimal = 0;

      for (int i = 0; i < addrArray.length; i++) {

         int power = 3 - i;
         ipDecimal += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
      }

      return String.valueOf(ipDecimal);
   }

   /**
    *
    * @param ip
    * @return
    */
   private long convertIpToDecimalLongh(String ip) {
      String ipAddress = ip;
      String[] addrArray = ipAddress.split("\\.");

      long ipDecimal = 0;

      for (int i = 0; i < addrArray.length; i++) {

         int power = 3 - i;
         ipDecimal += ((Integer.parseInt(addrArray[i]) % 256 * Math.pow(256, power)));
      }

      return ipDecimal;
   }

   /**
    *
    * @param startIP
    * @param endIP
    * @param lookUpIP
    * @return
    */
   private boolean compareToLookUp(long startIP, long endIP, long lookUpIP) {
      return (startIP<=lookUpIP && lookUpIP<=endIP);
   }

   /**
    *
    * @param lookUpIP
    * @return
    */
   private int lookUp(long lookUpIP) {
      String route = "-99";
      boolean done = false;
      int i = 0;

      while (i < storage.size() && !done) {
         if (compareToLookUp(convertIpToDecimalLongh(storage.get(i)[0]), convertIpToDecimalLongh(storage.get(i)[1]), lookUpIP)) {
            done = true;
            if (done) {
               route =(storage.get(i)[3]);
            }
         }
         i++;
      }
      if (!done) {
         route = "0";
      }
      return Integer.parseInt(route);
   }

   private boolean contains(long lookUpIp) {
      boolean found = false;
      int i = 0;

      while (i < storage.size() && !found) {
         if (compareToLookUp(convertIpToDecimalLongh(storage.get(i)[0]), convertIpToDecimalLongh(storage.get(i)[1]), lookUpIp)) {
            found = true;
         }
         i++;
      }
      return found;
   }

   private int delSearch(String ip) {
      boolean found = false;
      int i = 0;
      int index = -99;

      while (i < storage.size() && !found) {
         if (compareToLookUp(convertIpToDecimalLongh(storage.get(i)[0]), convertIpToDecimalLongh(storage.get(i)[1]), convertIpToDecimalLongh(ip))) {
            found = true;
            if (found) {
               index = i;
            }
         }
         i++;
      }
      return index;
   }

   /**
    *
    * @param ipAddress
    * @return
    * @throws UnknownHostException
    */
   private boolean isInRange(String ipAddress) throws UnknownHostException {
      InetAddress address = InetAddress.getByName(ipAddress);
      BigInteger start = new BigInteger(1, this.startAddress.getAddress());
      BigInteger end = new BigInteger(1, this.endAddress.getAddress());
      BigInteger target = new BigInteger(1, address.getAddress());

      int ipStartingRange = start.compareTo(target);
      int ipEndingRange = target.compareTo(end);

      return (ipStartingRange == -1 || ipStartingRange == 0) && (ipEndingRange == -1 || ipEndingRange == 0);
   }

   /**
    *
    * @param cmd
    * @return
    * @throws UnknownHostException
    */
   public int parseCmd(String cmd) throws UnknownHostException {
      if (cmd.contains("ADD") || cmd.contains("DEL")) {
         commandSplit(cmd);
      }
      else
      {
         _operationCmd = "LOOKUP";
      }

      //calculate();

      switch (_operationCmd)
      {
         case "ADD":
            calculate();
            addIpRangesWithMaskRoute(sIP, eIP, _mask, _routerRoute, _cidrNotation);
            System.out.println(_route = 1);
            break;

         case "DEL":
            if (contains(convertIpToDecimalLongh(_ipAddress))) {
               int index = delSearch(_ipAddress);
               storage.remove(index);
               System.out.println(_route = 1);
            }
            else {
               System.out.println(_route = 0);
            }
            break;

         case "LOOKUP":
            _route = lookUp(convertIpToDecimalLongh(_ipAddress = lookUpSplit(cmd)));
            System.out.println(_route);
            break;

      } // switch

      return _route;
   } // parseCmd


   /**
    * @param args
    * @throws java.net.UnknownHostException
    */
   public static void main(String[] args) throws UnknownHostException {
      Router router = new Router();
      //router.add("157.29.32.0/20, route=1");
      router.parseCmd("ADD prefix=157.29.32.0/20, route=1");
      router.parseCmd("ADD prefix=157.29.48.0/21, route=2");
      router.parseCmd("ADD prefix=157.29.48.0/20, route=3");
      router.parseCmd("ADD prefix=157.0.0.0/8, route=4");
      router.parseCmd("ADD prefix=157.29.62.0/23, route=NULL");
      router.parseCmd("ADD prefix=0/0, route=5");
      router.parseCmd("LOOKUP dest=157.29.32.95");
      router.parseCmd("LOOKUP dest=157.29.56.111");
      router.parseCmd("LOOKUP dest=157.29.40.17");
      router.parseCmd("LOOKUP dest=157.29.49.20");
      router.parseCmd("LOOKUP dest=157.30.56.111");
      router.parseCmd("LOOKUP dest=128.61.52.1");
      router.parseCmd("LOOKUP dest=157.29.63.1");
      router.parseCmd("DEL prefix=157.29.48.0/20, route=3");
      router.parseCmd("LOOKUP dest=157.29.61.2");
      router.parseCmd("DEL prefix=130.207.8.0/24,  route=1");
      router.parseCmd("DEL prefix=0.1.2.3/0, route=5");
      router.parseCmd("LOOKUP dest=130.207.8.102");
      router.parseCmd("LOOKUP dest=157.29.47.95");



   } // main
} // Router
