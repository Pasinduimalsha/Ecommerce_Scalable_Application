package com.sysco.inventory_service.mapper;

import com.sysco.inventory_service.dto.InventoryDTO;
import com.sysco.inventory_service.entity.Inventory;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {
    
    public InventoryDTO toDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }
        
        return new InventoryDTO(
                inventory.getId(),
                inventory.getSku(),
                inventory.getQuantity()
        );
    }
    
    public Inventory toEntity(InventoryDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Inventory inventory = new Inventory();
        inventory.setId(dto.getId());
        inventory.setSku(dto.getSku());
        inventory.setQuantity(dto.getQuantity());
        
        return inventory;
    }
}
