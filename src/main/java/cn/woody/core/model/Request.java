package cn.woody.core.model;

import java.util.Arrays;

/**
 * RPC 请求体
 * 
 * Title: RpcRequest<br> 
 * Description: RpcRequest<br> 
 * CreateDate:2017年7月28日 下午5:39:19 
 * @author woody
 */
public class Request extends RpcMessage {
    private String interfaceName;			// 接口名称
    private String methodName;				// 方法名称
	private Object[] parametersVal;			// 参数列表
    
    private Request(String interfaceName2, String methodName2, Object[] parametersVal2) {
    	this.interfaceName = interfaceName2;
    	this.methodName = methodName2;
    	this.parametersVal = parametersVal2;
	}

	public static Request build(String interfaceName, String methodName, Object[] parametersVal) {
    	return new Request(interfaceName, methodName, parametersVal);
    }
    
	public String getInterfaceName() {
		return interfaceName;
	}
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Object[] getParametersVal() {
		return parametersVal;
	}
	public void setParametersVal(Object[] parametersVal) {
		this.parametersVal = parametersVal;
	}
	public String toString() {
		return "Request [interfaceName=" + interfaceName + ", methodName=" + methodName + ", parametersVal=" + Arrays.toString(parametersVal) + "]";
	}
}
