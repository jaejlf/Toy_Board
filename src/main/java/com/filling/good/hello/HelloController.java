package com.filling.good.hello;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public Object hello() {

        Map<String, String> map = new HashMap<>();
        map.put("msg", "hello");

        return map;
    }

    @PostMapping("/bye/{msg}")
    public Object bye(@PathVariable String msg) {

        Map<String, String> map = new HashMap<>();
        map.put("msg", msg);

        return map;
    }

}
