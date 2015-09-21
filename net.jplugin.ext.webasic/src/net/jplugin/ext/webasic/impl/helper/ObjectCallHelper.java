package net.jplugin.ext.webasic.impl.helper;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.jplugin.common.kits.ReflactKit;
import net.jplugin.common.kits.StringKit;
import net.jplugin.core.ctx.api.RuleServiceFactory;
import net.jplugin.ext.webasic.api.ObjectDefine;
import net.jplugin.ext.webasic.impl.WebDriver;

/**
 *
 * @author: LiuHang
 * @version ����ʱ�䣺2015-2-3 ����05:51:42
 **/

public class ObjectCallHelper{
	public static class ObjectAndMethod {
		public Object object;
		public Method method;
	}

	Object svcObject = null;
	Map<String,Method> methodMap = new Hashtable<String, Method>();
	ObjectDefine objeceDefine;
	
	/**
	 * @param value
	 */
	public ObjectCallHelper(ObjectDefine d) {
		this.objeceDefine = d;
	}
	
	
	public ObjectDefine getObjeceDefine() {
		return objeceDefine;
	}


	/**
	 * ���argΪ�գ���ʾֻ��ͨ����Ӧ�ķ������������ǲ�������ȡ��һ�������������׳��쳣��
	 * @param req
	 * @param arg
	 * @return
	 */
	public ObjectAndMethod get(String requestMethodName,Class[] arg){
		initObject();
		ObjectAndMethod ret = new ObjectAndMethod();
		ret.object = this.svcObject;
		ret.method = getMethod(requestMethodName,arg);
		return ret;
	}

	public void initObject(){
		//��ʼ��svcObject
		if (svcObject==null){
			synchronized (this) {
				if (svcObject == null){
					svcObject = createObject(objeceDefine);	
				}
			}
		}
	}

	/**
	 * @param objeceDefine2
	 * @return
	 */
	private static Object createObject(ObjectDefine od) {
			Object objType = od.getObjType();
			Class objClass = od.getObjClass();
			String blName =od.getBlName();
			
			if (ObjectDefine.OBJ_JAVAOBJECT.equals(objType)){
				if (objClass == null){
					throw new RuntimeException("the service object class is null");
				}
				try {
					return objClass.newInstance();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}else if (ObjectDefine.OBJ_BIZLOGIC.equals(objType)){
				if (StringKit.isNull(blName)){
					throw new RuntimeException("the bizlogic name is null");
				}
				Object obj = RuleServiceFactory.getRuleService(blName);
				if (obj == null){
					throw new RuntimeException("can't find object:"+blName);
				}
				return obj;
			}else{
				throw new RuntimeException("Error obj type :"+objType);
			}
	}

	private Method getMethod(String reqMethodName,Class[] arg) {
		//��ȡMethodName
		String methodName = objeceDefine.getMethodName();
		
		if (StringKit.isNull(methodName)){
			methodName = reqMethodName;
		}
		if (StringKit.isNull(methodName)){
			throw new RuntimeException("method name is null for req method name:"+reqMethodName);
		}
		
		String methodKey = getMethodKey(methodName,arg);
		Method method = this.methodMap.get(methodKey);
		if (method ==null){
			synchronized (this) {
				method = this.methodMap.get(methodKey);
				if (method == null){
					if (this.svcObject == null){
						throw new RuntimeException("Object can't be null");
					}
					try {
						if (arg!=null){
							method = this.svcObject.getClass().getMethod(methodName, arg);
						}else{
							method = ReflactKit.findSingeMethodExactly(this.svcObject.getClass(),methodName);
						}
					} catch (Exception e) {
						throw new RuntimeException("Can't find method ["+methodName +"] with arg:"+ getString(arg) +" in "+svcObject.getClass().getName());
					}
					this.methodMap.put(methodKey, method);
				}
			}
		}
		return method;
	}


	
	/**
	 * @param arg
	 * @return
	 */
	private String getString(Class[] arg) {
		if (arg==null){
			return "";
		}
		
		StringBuffer ret = new StringBuffer();
		for (Class c:arg){
			ret.append(c.getName());
		}
		return ret.toString();
	}

	/**
	 * @param methodName
	 * @param arg
	 * @return
	 */
	private String getMethodKey(String methodName, Class[] arg) {
		if (arg==null){
			return methodName;
		}
		
		StringBuffer ret = new StringBuffer(methodName);
		for (Class c:arg){
			ret.append("|").append(c.getName());
		}
		return ret.toString();
	}
}