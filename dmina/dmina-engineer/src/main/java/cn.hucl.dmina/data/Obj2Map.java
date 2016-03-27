package cn.hucl.dmina.data;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

public class Obj2Map {
	public static Map<String,String> Obj2Map(Object obj){
		Map<String, String> map=new Hashtable<String, String>(0);
		Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
        	try {
        		String methodName=f.getName().substring(0, 1).toUpperCase()+f.getName().substring(1,f.getName().length());
        		Method m ;
        		try {
        			m = obj.getClass().getMethod("get" + methodName);
				} catch (Exception e) {
					System.err.println("完啦");
					continue;
				}
        		
        		String val="";
        		Object returnVal=m.invoke(obj);
        		if(null!=returnVal){
        				val=(String)returnVal.toString();
        		}
				map.put(f.getName(),val);
			} catch (Exception e) {
				e.printStackTrace();  
			} 
         }
        for(Entry<String, String> entry:map.entrySet()){
        	System.out.println(entry.getKey()+":"+entry.getValue());
        }
        return map;
	}
	
	public static void main(String[] args) {
		UserInfo userInfo=new UserInfo();
		userInfo.setUser_name("hucl");
		userInfo.setPassword("hhhhh");
	    Obj2Map(userInfo);
	}
}
