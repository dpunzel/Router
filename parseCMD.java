
public class parseCMD {

	//Set constants
	private String _cmd;
	private String _ip;
	private int _mask;
	private int _beginningl;
	private int _ending;
	private int _route;
	
	public int parseCMD(String cmd)
	{
		cmd = "ADD prefix=X.X.X.X/Y, route=Z";
		_cmd = cmd;
		String[] result = cmd.split("\\s");
		
		_route = Integer.parseInt(result[-1]);
		
		return _route;
	}
	
	private void parseCommand(String fullCommand)
	{
		//ADD prefix=X.X.X.X/Y, route=Z//
		
	}


}
