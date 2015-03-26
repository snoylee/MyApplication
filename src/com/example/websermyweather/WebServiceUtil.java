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
	// ����Web Service�������ռ�
	private static final String SERVICE_NS="http://WebXml.com.cn/";
	// ����Web Service�ṩ�����URL
	private static final String SERVICE_URL =
			"http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx";
	// ����Զ��Web Service��ȡʡ���б�
	public static List<String> getProvinceList(){
		//���õķ���
		final String methodName ="getRegionProvince";
		// ����HttpTransportSE�������
		final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
		ht.debug = true;
		// ʹ��SOAP1.1Э�鴴��Envelop����
		final SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		// ʵ����SoapObject����
		SoapObject soapObject = new SoapObject(SERVICE_NS,methodName);
		envelop.bodyOut = soapObject;
		// ������.Net�ṩ��Web Service���ֽϺõļ�����
		envelop.dotNet = true;
		//�����߳�ȥִ��webService
		FutureTask<List<String>> task = new FutureTask<List<String>>(new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				// ����Web Service
				ht.call(SERVICE_NS+methodName,envelop);
				if(envelop.getResponse()!=null){
					// ��ȡ��������Ӧ���ص�SOAP��Ϣ
				SoapObject soapResult= (SoapObject) envelop.bodyIn;
				SoapObject detail = (SoapObject) soapResult.getProperty(methodName+"Result");
				// ������������Ӧ��SOAP��Ϣ��
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
	// ����ʡ�ݻ�ȡ�����б�
	public static List<String>  getCityListByProvince(String province){
		// ���õķ���
		final String methodName ="getSupportCityString"; 
		// ����HttpTransportSE�������
		final HttpTransportSE  ht = new HttpTransportSE(SERVICE_URL);
		ht.debug = true;
		//// ʵ����SoapObject����
		SoapObject soapObject = new SoapObject(SERVICE_NS, methodName);
		//���һ���������
		soapObject.addProperty("theRegionCode", province);
		// ʹ��SOAP1.1Э�鴴��Envelop����
		final SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelop.bodyOut = soapObject;
		// ������.Net�ṩ��Web Service���ֽϺõļ�����
		envelop.dotNet = true;
		//�����߳�ȥ����Webservice
		FutureTask<List<String>>  task = new FutureTask<List<String>>(new Callable<List<String>>() {
			@Override
			public List<String> call() throws Exception {
				// ����Web Service
				ht.call(SERVICE_NS+methodName, envelop);
				if(envelop.getResponse()!=null){
					// ��ȡ��������Ӧ���ص�SOAP��Ϣ
					SoapObject result = (SoapObject) envelop.bodyIn;
					SoapObject detail = (SoapObject) result.getProperty(methodName+"Result");
					// ������������Ӧ��SOAP��Ϣ��
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
			// ������ÿ��ʡ��
			result.add(detail.getProperty(i).toString().split(",")[0]);
		}
		return result;
	}
	//ͨ�����л���������
	public static SoapObject getWeatherByCity(String cityName){
		final String methodName = "getWeather";
		final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
		ht.debug = true;
		final SoapSerializationEnvelope envelop = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		SoapObject soapObject = new SoapObject(SERVICE_NS,methodName);
		soapObject.addProperty("theCityCode", cityName);
		envelop.bodyOut = soapObject;
		// ������.Net�ṩ��Web Service���ֽϺõļ�����
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
