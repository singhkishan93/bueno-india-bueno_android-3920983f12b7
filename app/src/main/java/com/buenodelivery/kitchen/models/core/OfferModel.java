package com.buenodelivery.kitchen.models.core;

/**
 * Created by bedi on 21/02/16.
 */
public class OfferModel {
    public String offerText;
    public String offerCode;

    private OfferModel() {

    }

    public static Builder getBuilder() {
        return new Builder();
    }

    public static class Builder {
        private OfferModel offerModel;

        public Builder() {
            offerModel = new OfferModel();
        }

        public Builder setOfferCode(String offerCode) {
            offerModel.offerCode = offerCode;
            return this;
        }

        public Builder setOfferText(String offerText) {
            offerModel.offerText = offerText;
            return this;
        }

        public OfferModel build() {
            return offerModel;
        }
    }
}
