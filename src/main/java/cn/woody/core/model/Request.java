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
    
	public static Request build(String interfaceName, String methodName, Object[] parametersVal) {
    	return new Request().setInterfaceName(interfaceName).setMethodName(methodName).setParametersVal(parametersVal);
    }
    
	public Request setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
		return this;
	}
	public Request setMethodName(String methodName) {
		this.methodName = methodName;
		return this;
	}
	public Request setParametersVal(Object[] parametersVal) {
		this.parametersVal = parametersVal;
		return this;
	}
	public String getInterfaceName() {
		return interfaceName;
	}
	public String getMethodName() {
		return methodName;
	}
	public Object[] getParametersVal() {
		return parametersVal;
	}
	public String toString() {
		return "Request [interfaceName=" + interfaceName + ", methodName=" + methodName + ", parametersVal=" + Arrays.toString(parametersVal) + "]";
	}
}
