package com.wzk.mvc.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

@Controller
@RequestMapping("/qrcode")
public class QRCodeController {
	
	@RequestMapping("/getqrcode")
	public void getqrcode(HttpServletRequest request, HttpServletResponse response){
		
        ByteArrayOutputStream out = QRCode.from("www.baidu.com").to(ImageType.PNG).stream();
        response.setContentType("image/png");
        response.setContentLength(out.size());
        try {
			OutputStream outStream = response.getOutputStream();
			outStream.write(out.toByteArray());
			outStream.flush();
			outStream.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}
