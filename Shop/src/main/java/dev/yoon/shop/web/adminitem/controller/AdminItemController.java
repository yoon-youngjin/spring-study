package dev.yoon.shop.web.adminitem.controller;

import dev.yoon.shop.domain.item.entity.Item;
import dev.yoon.shop.global.config.security.UserDetailsImpl;
import dev.yoon.shop.global.error.exception.ErrorCode;
import dev.yoon.shop.web.adminitem.dto.ItemFormDto;
import dev.yoon.shop.web.adminitem.service.AdminItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/admin/items")
@RequiredArgsConstructor
public class AdminItemController {

    private final AdminItemService adminItemService;

    @GetMapping("/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "adminitem/registeritemform";
    }

    @PostMapping("/new")
    private String itemNew(
            @Valid @ModelAttribute("itemFormDto") ItemFormDto dto,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            RedirectAttributes redirectAttributes
    ) {

        if (dto.getItemImageFiles().get(0).isEmpty()) {
            bindingResult.reject("requiredFirstItemImage", ErrorCode.REQUIRED_REPRESENT_IMAGE.getMessage());
            return "adminitem/registeritemform";
        }
        if (bindingResult.hasErrors()) {
            return "adminitem/registeritemform";
        }

        String email = userDetails.getUsername();
        try {
            Item savedItem = adminItemService.saveItem(dto, email);
        } catch (Exception e) {
            log.error(e.getMessage());
            bindingResult.reject("globalError", "상품 등록 중 에러가 발생하였습니다.");
            return "adminitem/registeritemform";
        }

        return "redirect:/";
    }

}
