package cn.bdqn.gulimall.product.exception;

import cn.bdqn.gulimall.common.utils.R;
import cn.bdqn.gulimall.exection.BizExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 刚
 * @version 1.0.1
 * @date 2022/4/22
 * RestControllerAdvice注解相当于
 * ResponseBody
 * ControllerAdvice
 *
 *
 */
@RestControllerAdvice(basePackages = "cn.bdqn.gulimall.product.controller")
@Slf4j
public class ValidControllerAdvice {

    /**
     * 处理参数格式校验失败
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R validException(MethodArgumentNotValidException e) {
        BindingResult res = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        res.getFieldErrors().forEach(fieldError -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });


        return R.error(BizExceptionCode.VALID_EXCEPTION.getCode(), BizExceptionCode.VALID_EXCEPTION.getMsg()).put("data", errorMap);
    }

    @ExceptionHandler(Throwable.class)
    public R otherException(Throwable throwable) {
        log.error("其他异常{}", throwable);
        return R.error(BizExceptionCode.UNKNOW_EXCEPTION.getCode(), BizExceptionCode.UNKNOW_EXCEPTION.getMsg());
    }

}
