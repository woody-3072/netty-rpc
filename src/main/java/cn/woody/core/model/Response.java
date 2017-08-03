package cn.woody.core.model;

/**
 * RPC响应体
 * 
 * Title: RpcResponse<br> 
 * Description: RpcResponse<br> 
 * CreateDate:2017年7月28日 下午5:39:33 
 * @author woody
 */
public class Response extends RpcMessage {
	private String errorMessage;			// 错误信息
	private Object data;				// 数据
	
	public static Response buildSuccess (Object data) {
		return new Response().setData(data);
	}
	public static Response buildError(String error) {
		return new Response().setErrorMessage(error);
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	public Object getData() {
		return data;
	}
	public Response setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		return this;
	}
	public Response setData(Object data) {
		this.data = data;
		return this;
	}
	public String toString() {
		return "Response [errorMessage=" + errorMessage + ", data=" + data + "]";
	}
}