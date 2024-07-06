package com.shopStudy.repository;

import com.shopStudy.dto.ItemSearchDto;
import com.shopStudy.dto.MainItemDto;
import com.shopStudy.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
