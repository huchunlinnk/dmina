package cn.hucl.dmina.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetClassProperties {
	/**
	 * @author hucl
	 * @since 2013-12-19
	 * @param model
	 * @return retrieve the set of all properties
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static List<MessageFieldInfo> getAllProperties(Object model)
			throws NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		List<MessageFieldInfo> list = new ArrayList<MessageFieldInfo>();
		Serializable keyValue = null;
		Field[] field = model.getClass().getDeclaredFields();
		for (int j = 0; j < field.length; j++) {
			MessageFieldInfo messageFieldInfo = new MessageFieldInfo();
			String name = field[j].getName();
//			System.out.println("attribute name:" + name);
			String type = field[j].getGenericType().toString();
			String fieldInfoType = null;
			name = new GetClassProperties().getNameStrAfterGet(name);

			if (type.equals("class java.lang.String")) {
				Method m = model.getClass().getMethod("get" + name);
				fieldInfoType = "String";
				keyValue = (String) m.invoke(model);
			} else if (type.equals("class java.lang.Integer")) {
				Method m = model.getClass().getMethod("get" + name);
				fieldInfoType = "Integer";
				keyValue = (Integer) m.invoke(model);
			} else if (type.equals("class java.lang.Short")) {
				Method m = model.getClass().getMethod("get" + name);
				fieldInfoType = "Short";
				keyValue = (Short) m.invoke(model);
			} else if (type.equals("class java.lang.Double")) {
				Method m = model.getClass().getMethod("get" + name);
				fieldInfoType = "Double";
				keyValue = (Double) m.invoke(model);
			} else if (type.equals("class java.lang.Boolean")) {
				Method m = model.getClass().getMethod("get" + name);
				fieldInfoType = "Boolean";
				keyValue = (Boolean) m.invoke(model);
			} else if (type.equals("class java.util.Date")) {
				Method m = model.getClass().getMethod("get" + name);
				fieldInfoType = "Date";
				keyValue = (Date) m.invoke(model);
			} else if (type.equals("class java.lang.Byte")) {
				Method m = model.getClass().getMethod("get" + name);
				fieldInfoType = "Byte";
				keyValue = (Byte) m.invoke(model);
			}
			// byte array
			else if (type.equals("class [B")) {
				Method m = model.getClass().getMethod("get" + name);
				fieldInfoType = "Bytes";
				keyValue = (byte[]) m.invoke(model);
			} else {
				try {
					throw new Exception("unsupported parameter type");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			messageFieldInfo.setName(name);
			messageFieldInfo.setType(fieldInfoType);
			messageFieldInfo.setValue(keyValue);
			list.add(messageFieldInfo);
		}
		return list;
	}

	public String getNameStrAfterGet(String name) {
		return name.substring(0, 1).toUpperCase()
				+ name.subSequence(1, name.length());
	}

}

class Person {
	private int id;
	private String name;
	private int dept;
	private double salary;
	private long distance;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDept() {
		return dept;
	}

	public void setDept(int dept) {
		this.dept = dept;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public long getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}
}
