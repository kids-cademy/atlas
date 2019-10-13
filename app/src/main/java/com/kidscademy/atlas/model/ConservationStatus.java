package com.kidscademy.atlas.model;

public enum ConservationStatus {
	/**
	 * Not Available. Not enough data to make an assessment of its risk of extinction or species has not yet been evaluated
	 * against the criteria.
	 */
	NA,
	/**
	 * Least Concern. Lowest risk; does not qualify for a higher risk category. Widespread and abundant taxa are included in
	 * this category.
	 */
	LC,
	/** Near Threatened. Likely to become endangered in the near future. */
	NT,
	/** Vulnerable. High risk of endangerment in the wild. */
	VU,
	/** Endangered. High risk of extinction in the wild. */
	EN,
	/** Critically Endangered. Extremely high risk of extinction in the wild. */
	CR,
	/** Extinct in the Wild. Known only to survive in captivity, or as a naturalized population outside its historic range. */
	EW,
	/** Extinct. No known individuals remaining. */
	EX;

	public boolean isLowRisk() {
		return this == LC || this == NT;
	}

	public boolean isThreatened() {
		return this == VU || this == EN || this == CR;
	}

	public boolean isExtinct() {
		return this == EW || this == EX;
	}
}
