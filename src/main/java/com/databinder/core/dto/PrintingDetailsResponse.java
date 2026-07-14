package com.databinder.core.dto;

import java.util.List;

import com.databinder.core.enums.AlertType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PrintingDetailsResponse 
{
	private Long id;
	private String cardName;
	private String cardSetName;
	private String collectorNumber;
	private String rarity;
	private String imageUrl;
	
	private List<PriceSnapshotResponse> priceSnapshots;
}
