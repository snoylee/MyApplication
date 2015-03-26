package com.example.websermyweather;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class WebServiceUtil {
	// 定义Web Service的命名空间
	private static final String SERVICE_NS="http://WebXml.com.cn/";
	// 定义Web Service提供服务的URL
	private static final String SERVICE_URL =
			"http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx";
	// 调用远程Web Service获取省份列表
	public static List<String> getProvinceList(){
		//调用的方法
		final String methodName ="getRegionProvince";
		// 创建HttpTransportSE传输对象
		final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
		ht.debug = true;
		// 使用SOAP1.1协议创建Envelop对象
		final SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		// 实例化SoapObject对象
		SoapObject soapObject = new SoapObject(SERVICE_NS,methodName);
		envelop.bodyOut = soapObject;
		// 设置与.Net提供的Web Service保持较好的兼容性
		envelop.dotNet = true;
		//定义线程去执行webService
		FutureTask<List<String>> task = new FutureTask<List<String>>(new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				// 调用Web Service
				ht.call(SERVICE_NS+methodName,envelop);
				if(envelop.getResponse()!=null){
					// 获取服务器响应返回的SOAP消息
				SoapObject soapResult= (SoapObject) envelop.bodyIn;
				SoapObject detail = (SoapObject) soapResult.getProperty(methodName+"Result");
				// 解析服务器响应的SOAP消息。
				return parseProvinceOrCity(detail);
				}
				return null;
			}
		});
		new Thread(task).start();
		try {
			return task.get();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	// 根据省份获取城市列表
	public static List<String>  getCityListByProvince(String province){
		// 调用的方法
		final String methodName ="getSupportCityString"; 
		// 创建HttpTransportSE传输对象
		final HttpTransportSE  ht = new HttpTransportSE(SERVICE_URL);
		ht.debug = true;
		//// 实例化SoapObject对象
		SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
		//添加一条请求参数
		soapObject.addProperty("theRegionCode", province);
		// 使用SOAP1.1协议创建Envelop对象
		final SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelop.bodyOut = soapObject;
		// 设置与.Net提供的Web Service保持较好的兼容性
		envelop.dotNet = true;
		//定义线程去调用Webservice
		FutureTask<List<String>>  task = new FutureTask<List<String>>(new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				// 调用Web Service
				ht.call(SERVICE_NS+methodName, envelop);
				if(envelop.getResponse()!=null){
					// 获取服务器响应返回的SOAP消息
					SoapObject result = (SoapObject) envelop.bodyIn;
					SoapObject detail = (SoapObject) result.getProperty(methodName+"Result");
					// 解析服务器响应的SOAP消息。
					return parseProvinceOrCity(detail);
				}
				return null;
			}
		});
		new Thread(task).start();
		try {
			return task.get();
		} 
		 catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static  List<String> parseProvinceOrCity(SoapObject detail) {
		ArrayList<String> result = new ArrayList<String>();
		Log.v("detail", String.valueOf(detail));
		for(int i = 0;i<detail.getPropertyCount();i++){
			// 解析出每个省份
			result.add(detail.getProperty(i).toString().split(",")[0]);
		}
		return result;
	}
	//通过城市获得天气情况
	public static SoapObject getWeatherByCity(String cityName){
		final String methodName = "getWeather";
		final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
		ht.debug = true;
		final SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		SoapObject soapObject = new SoapObject(SERVICE_NS,methodName);
		soapObject.addProperty("theCityCode", cityName);
		envelop.bodyOut = soapObject;
		// 设置与.Net提供的Web Service保持较好的兼容性
		envelop.dotNet = true;
		FutureTask<SoapObject> task = new FutureTask<SoapObject>(new Callable<SoapObject>() {
			@Override
			public SoapObject call() throws Exception {
				ht.call(SERVICE_NS + methodName, envelop);
				SoapObject result = (SoapObject) envelop.bodyIn;
				SoapObject detail = (SoapObject) result.getProperty(
					methodName + "Result");
				Log.v("detail", String.valueOf(detail));
				return detail;
			}
		});
		new Thread(task).start();
		try {
			return task.get();
		} 
			catch (Exception e) {
			e.printStackTrace();
		}
		return null;	
	}
}
