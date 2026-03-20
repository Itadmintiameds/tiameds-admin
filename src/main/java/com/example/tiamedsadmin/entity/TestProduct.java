package com.example.tiamedsadmin.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonPropertyOrder({
        "therapeuticCategory",
        "therapeuticSubCategory",
        "productName",
        "molecule",
        "mechanismOfAction",
        "primaryUse",
        "dosageForm",
        "strengthNetQuantity",
        "warningsPrecautions",
        "productDescription",
        "productImageUrl",
        "productMarketingUrl",
        "packagingUnit",
        "numberOfUnits",
        "packSize",
        "minimumOrderQty",
        "maxOrderQty",
        "batchLotNumber",
        "manufacturingDate",
        "expiryDate",
        "storageCondition",
        "stockQuantity",
        "dateOfEntry",
        "pricePerUnit",
        "mrpInr",
        "discountPercent",
        "additionalDiscount",
        "finalPrice",
        "gstPercent",
        "hsnCode"
})
public class TestProduct {

    @JsonProperty("therapeuticCategory")
    private String therapeuticCategory;

    @JsonProperty("therapeuticSubCategory")
    private String therapeuticSubCategory;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("molecule")
    private String molecule;

    @JsonProperty("mechanismOfAction")
    private String mechanismOfAction;

    @JsonProperty("primaryUse")
    private String primaryUse;

    @JsonProperty("dosageForm")
    private String dosageForm;

    @JsonProperty("strengthNetQuantity")
    private String strengthNetQuantity;

    @JsonProperty("warningsPrecautions")
    private String warningsPrecautions;

    @JsonProperty("productDescription")
    private String productDescription;

    @JsonProperty("productImageUrl")
    private String productImageUrl;

    @JsonProperty("productMarketingUrl")
    private String productMarketingUrl;

    @JsonProperty("packagingUnit")
    private String packagingUnit;

    @JsonProperty("numberOfUnits")
    private Integer numberOfUnits;

    @JsonProperty("packSize")
    private Integer packSize;

    @JsonProperty("minimumOrderQty")
    private Integer minimumOrderQty;

    @JsonProperty("maxOrderQty")
    private Integer maxOrderQty;

    @JsonProperty("batchLotNumber")
    private String batchLotNumber;

    @JsonProperty("manufacturingDate")
    private String manufacturingDate;

    @JsonProperty("expiryDate")
    private String expiryDate;

    @JsonProperty("storageCondition")
    private String storageCondition;

    @JsonProperty("stockQuantity")
    private Integer stockQuantity;

    @JsonProperty("dateOfEntry")
    private String dateOfEntry;

    @JsonProperty("pricePerUnit")
    private Double pricePerUnit;

    @JsonProperty("mrpInr")
    private Double mrpInr;

    @JsonProperty("discountPercent")
    private Double discountPercent;

    @JsonProperty("additionalDiscount")
    private Double additionalDiscount;

    @JsonProperty("finalPrice")
    private Double finalPrice;

    @JsonProperty("gstPercent")
    private Double gstPercent;

    @JsonProperty("hsnCode")
    private String hsnCode;
}