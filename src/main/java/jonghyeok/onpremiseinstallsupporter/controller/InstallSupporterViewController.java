package jonghyeok.onpremiseinstallsupporter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class InstallSupporterViewController {

    @GetMapping("/")
    public String mainView() {
        return "index";
    }
}
