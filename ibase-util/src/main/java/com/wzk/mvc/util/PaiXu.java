package com.wzk.mvc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

class VO{
	/*private java.lang.Integer a;
	private java.lang.Integer b;
	private java.lang.Integer c;
	public java.lang.Integer getA() {
		return a;
	}
	public void setA(java.lang.Integer a) {
		this.a = a;
	}
	public java.lang.Integer getB() {
		return b;
	}
	public void setB(java.lang.Integer b) {
		this.b = b;
	}
	public java.lang.Integer getC() {
		return c;
	}
	public void setC(java.lang.Integer c) {
		this.c = c;
	}
	public VO(java.lang.Integer a,java.lang.Integer b,java.lang.Integer c){
		this.a = a;
		this.b = b;
		this.c = c;
	}*/
	public VO(int a,String b,Date c,int d,String e){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
	}
	private int a;//年龄
	private String b;//姓名
	private Date c;//时间
	private int d;//
	private String e;//
	
	public int getA() {
		return a;
	}

	public void setA(int a) {
		this.a = a;
	}

	public String getB() {
		return b;
	}

	public void setB(String b) {
		this.b = b;
	}

	public Date getC() {
		return c;
	}

	public void setC(Date c) {
		this.c = c;
	}

	public int getD() {
		return d;
	}

	public void setD(int d) {
		this.d = d;
	}

	public String getE() {
		return e;
	}

	public void setE(String e) {
		this.e = e;
	}
	SimpleDateFormat  sdf = new SimpleDateFormat ("yyyy-MM-dd");
	@Override
	public String toString() {
		return String.format("E:%s,D:%d,C:%s,A:%s,B:%s", this.e,this.d,sdf.format(this.c),this.a,this.b);
	}
}

public class PaiXu {
	public static void main(String[] args) throws ParseException {
		SimpleDateFormat  sdf = new SimpleDateFormat ("yyyy-MM-dd");
		List<VO> vos = new ArrayList<>();
//		vos.add(new VO(1, "张三",  sdf.parse("2016-05-06")));
//		vos.add(new VO(1, "张三",  sdf.parse("2015-05-06")));
//		vos.add(new VO(4, "李四",  sdf.parse("2017-05-06")));
//		vos.add(new VO(9, "马六",  sdf.parse("2013-05-06")));
		
		vos.add(new VO(1, "张金龙", sdf.parse("1988-12-01"), 28, "山西临汾"));
		vos.add(new VO(2, "杨靖涛", sdf.parse("1990-04-01"), 27, "北京海淀"));
		vos.add(new VO(3, "王文强", sdf.parse("1992-10-23"), 21, "山西临汾"));
		vos.add(new VO(4, "魏中科1",sdf.parse("1980-12-01"), 30, "北京通州"));
		vos.add(new VO(4, "魏中科2",sdf.parse("1980-12-01"), 30, "北京通州"));
		vos.add(new VO(5, "王维奇", sdf.parse("1980-12-01"), 28, "山西临汾"));
		vos.add(new VO(6, "王老五", sdf.parse("1981-12-01"), 30, "北京通州"));
		
		Collections.sort(vos,new Comparator<VO>() {
			@Override
			public int compare(VO o1, VO o2) {//按照年龄升序，姓名倒序，年份倒序
				/*if(o1.getA()==o2.getA()){
					if(o1.getB().compareTo(o1.getB())==0){
						if(o1.getC().getTime()-o2.getC().getTime()==0){
							return 0;
						}else if(o1.getC().getTime()-o2.getC().getTime()>0){
							return 1;
						}else{
							return -1;
						}
					}else{
						return o1.getB().compareTo(o2.getB());
					}
				}else{
					return o1.getA()-o2.getA();
				}*/
				if(o1.getE().compareTo(o2.getE())==0){//籍贯
					if(o1.getD()==o2.getD()){//年龄
						return o1.getC().compareTo(o2.getC());
					}else{
						return o1.getD()-o2.getD();
					}
				}else{
					return o1.getE().compareTo(o2.getE());
				}
				
				/*if(o1.getC().compareTo(o2.getC())==0){
					if(o1.getD()==o2.getD()){
						if(o1.getE().compareTo(o2.getE())==0){
							return o1.getB().compareTo(o2.getB());
						}else{
							return o1.getE().compareTo(o2.getE());
						}
					}else{
						return o1.getD()-o2.getD();
					}
				}else{
					return o1.getC().compareTo(o2.getC());
				}*/
			}
		});
		for (VO vo : vos) {
			System.out.println(vo.toString());
		}
	}
}