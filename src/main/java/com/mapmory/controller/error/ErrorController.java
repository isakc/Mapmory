package com.mapmory.controller.error;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
	
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) throws Exception {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorMessage", "요청하신 페이지를 찾을 수 없습니다.");
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("errorMessage", "서버 오류가 발생했습니다.");
            } else {
                model.addAttribute("errorMessage", "오류가 발생했습니다.");
            }
            
            model.addAttribute("statusCode", statusCode);
        }
        
        return "/common/error";
    }
    
    public String getErrorPath() throws Exception {
        return "/error";
    }
}