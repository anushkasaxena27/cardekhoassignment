package com.cardekho.seed;

import com.cardekho.config.SeedProperties;
import com.cardekho.entity.*;
import com.cardekho.repository.CarMakeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@Order(1)
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements ApplicationRunner {

	private static final String[] IMAGE_POOL = {
			"https://images.unsplash.com/photo-1492144534655-ae79c964c9d7?w=1200&q=80",
			"https://images.unsplash.com/photo-1502877338535-766e1452684a?w=1200&q=80",
			"https://images.unsplash.com/photo-1542362567-b07e54358753?w=1200&q=80",
			"https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=1200&q=80",
			"https://images.unsplash.com/photo-1511919884223-f6913805400b?w=1200&q=80",
			"https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Car_1952.jpg/1200px-Car_1952.jpg"
	};

	private final CarMakeRepository carMakeRepository;
	private final SeedProperties seedProperties;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (!seedProperties.isEnabled()) {
			log.info("Database seeding disabled (app.seed.enabled=false)");
			return;
		}
		if (carMakeRepository.count() > 0) {
			log.info("Database already seeded; skipping.");
			return;
		}
		log.info("Seeding catalog (100+ variants, reviews, images)...");
		List<CarMake> makes = buildCatalog();
		carMakeRepository.saveAll(makes);
		log.info("Seeding complete. Makes persisted: {}", makes.size());
	}

	private List<CarMake> buildCatalog() {
		List<CarMake> makes = new ArrayList<>();
		String[][] brandRows = {
				{ "Hyundai", "South Korea", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/Hyundai_Motor_Company_logo.svg/320px-Hyundai_Motor_Company_logo.svg.png" },
				{ "Tata", "India", "https://upload.wikimedia.org/wikipedia/en/thumb/8/8e/Tata_logo.svg/320px-Tata_logo.svg.png" },
				{ "Mahindra", "India", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/60/Mahindra_Rise_Logo.svg/320px-Mahindra_Rise_Logo.svg.png" },
				{ "Toyota", "Japan", "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e7/Toyota.svg/320px-Toyota.svg.png" },
				{ "Honda", "Japan", "https://upload.wikimedia.org/wikipedia/commons/thumb/7/76/Honda_Motor_Company.svg/320px-Honda_Motor_Company.svg.png" },
				{ "Kia", "South Korea", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/47/KIA_logo3.svg/320px-KIA_logo3.svg.png" },
				{ "Maruti Suzuki", "India", "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Maruti_Suzuki_Logo.svg/320px-Maruti_Suzuki_Logo.svg.png" },
				{ "MG", "China", "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1f/MG_Motor_India_logo.svg/320px-MG_Motor_India_logo.svg.png" },
				{ "Skoda", "Czech Republic", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f3/%C5%A0koda_Auto_logo_%282016%29.svg/320px-%C5%A0koda_Auto_logo_%282016%29.svg.png" },
				{ "Volkswagen", "Germany", "https://upload.wikimedia.org/wikipedia/commons/thumb/6/6d/Volkswagen_logo_2019.svg/320px-Volkswagen_logo_2019.svg.png" },
				{ "BMW", "Germany", "https://upload.wikimedia.org/wikipedia/commons/thumb/4/44/BMW.svg/320px-BMW.svg.png" },
				{ "Mercedes-Benz", "Germany", "https://upload.wikimedia.org/wikipedia/commons/thumb/9/90/Mercedes-Logo.svg/320px-Mercedes-Logo.svg.png" },
				{ "Audi", "Germany", "https://upload.wikimedia.org/wikipedia/commons/thumb/9/92/Audi-Logo_2016.svg/320px-Audi-Logo_2016.svg.png" },
				{ "Volvo", "Sweden", "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f9/Volvo_Cars_logo.svg/320px-Volvo_Cars_logo.svg.png" },
				{ "Lexus", "Japan", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/39/Lexus_division_emblem.svg/320px-Lexus_division_emblem.svg.png" }
		};

		String[][][] modelTemplates = {
				{ { "Creta", "SUV", "SUV", "Mid" }, { "Venue", "SUV", "SUV", "Mass" }, { "Verna", "Sedan", "Sedan", "Mid" }, { "i20", "Hatchback", "Hatchback", "Mass" } },
				{ { "Nexon", "SUV", "SUV", "Mid" }, { "Harrier", "SUV", "SUV", "Premium" }, { "Safari", "SUV", "SUV", "Premium" }, { "Tiago", "Hatchback", "Hatchback", "Mass" } },
				{ { "XUV700", "SUV", "SUV", "Premium" }, { "Scorpio N", "SUV", "SUV", "Mid" }, { "Thar", "SUV", "SUV", "Mid" }, { "Bolero", "SUV", "SUV", "Mass" } },
				{ { "Innova Hycross", "MUV", "MUV", "Premium" }, { "Fortuner", "SUV", "SUV", "Premium" }, { "Camry", "Sedan", "Sedan", "Luxury" }, { "Glanza", "Hatchback", "Hatchback", "Mass" } },
				{ { "City", "Sedan", "Sedan", "Mid" }, { "Elevate", "SUV", "SUV", "Mid" }, { "Amaze", "Sedan", "Sedan", "Mass" }, { "Jazz", "Hatchback", "Hatchback", "Mass" } },
				{ { "Seltos", "SUV", "SUV", "Mid" }, { "Sonet", "SUV", "SUV", "Mass" }, { "Carens", "MUV", "MUV", "Mid" }, { "EV6", "EV", "SUV", "Premium" } },
				{ { "Swift", "Hatchback", "Hatchback", "Mass" }, { "Baleno", "Hatchback", "Hatchback", "Mass" }, { "Grand Vitara", "SUV", "SUV", "Mid" }, { "Ertiga", "MUV", "MUV", "Mass" } },
				{ { "ZS EV", "EV", "SUV", "Mid" }, { "Hector", "SUV", "SUV", "Mid" }, { "Astor", "SUV", "SUV", "Mid" }, { "Gloster", "SUV", "SUV", "Premium" } },
				{ { "Kushaq", "SUV", "SUV", "Mid" }, { "Slavia", "Sedan", "Sedan", "Mid" }, { "Kodiaq", "SUV", "SUV", "Luxury" }, { "Superb", "Sedan", "Sedan", "Premium" } },
				{ { "Taigun", "SUV", "SUV", "Mid" }, { "Virtus", "Sedan", "Sedan", "Mid" }, { "Tiguan", "SUV", "SUV", "Premium" }, { "ID.4", "EV", "SUV", "Premium" } },
				{ { "3 Series", "Sedan", "Sedan", "Luxury" }, { "X1", "SUV", "SUV", "Luxury" }, { "iX", "EV", "SUV", "Luxury" }, { "5 Series", "Sedan", "Sedan", "Luxury" } },
				{ { "C-Class", "Sedan", "Sedan", "Luxury" }, { "GLC", "SUV", "SUV", "Luxury" }, { "EQB", "EV", "SUV", "Luxury" }, { "E-Class", "Sedan", "Sedan", "Luxury" } },
				{ { "A4", "Sedan", "Sedan", "Luxury" }, { "Q3", "SUV", "SUV", "Luxury" }, { "e-tron", "EV", "SUV", "Luxury" }, { "Q5", "SUV", "SUV", "Luxury" } },
				{ { "XC60", "SUV", "SUV", "Luxury" }, { "XC90", "SUV", "SUV", "Luxury" }, { "C40 Recharge", "EV", "SUV", "Luxury" }, { "S60", "Sedan", "Sedan", "Luxury" } },
				{ { "ES", "Sedan", "Sedan", "Luxury" }, { "NX", "SUV", "SUV", "Luxury" }, { "RX", "SUV", "SUV", "Luxury" }, { "RZ", "EV", "SUV", "Luxury" } }
		};

		for (int bi = 0; bi < brandRows.length; bi++) {
			String[] br = brandRows[bi];
			CarMake make = CarMake.builder().name(br[0]).country(br[1]).logoUrl(br[2]).build();
			String[][] models = modelTemplates[bi];
			for (String[] md : models) {
				CarModel model = CarModel.builder()
						.make(make)
						.modelName(md[0])
						.segment(md[3])
						.bodyType(md[2])
						.launchYear(2020 + ThreadLocalRandom.current().nextInt(0, 5))
						.build();
				make.getModels().add(model);
				for (int vi = 0; vi < 3; vi++) {
					model.getVariants().add(buildVariant(model, md, vi));
				}
			}
			makes.add(make);
		}
		return makes;
	}

	private CarVariant buildVariant(CarModel model, String[] md, int tierIndex) {
		boolean ev = "EV".equalsIgnoreCase(md[1]);
		String fuel = ev ? "Electric" : pickFuel(model.getModelName(), tierIndex);
		String transmission = ev ? "Automatic" : (tierIndex == 0 ? "Manual" : "Automatic");
		BigDecimal basePrice = basePriceFor(model.getSegment(), tierIndex, ev);
		int seat = model.getModelName().contains("Ertiga") || model.getModelName().contains("Carens") ? 7 : 5;
		int hp = ev ? 140 + tierIndex * 25 : 85 + tierIndex * 22;
		int torque = ev ? 250 + tierIndex * 40 : 120 + tierIndex * 25;
		int cc = ev ? 0 : 1100 + tierIndex * 250;
		BigDecimal mileage = ev ? BigDecimal.valueOf(9.2 + tierIndex * 0.4).setScale(1, RoundingMode.HALF_UP)
				: BigDecimal.valueOf(16 + tierIndex * 1.2).setScale(1, RoundingMode.HALF_UP);
		String variantName = tierIndex == 0 ? "Base" : (tierIndex == 1 ? "Mid" : "Top");
		String drive = model.getBodyType().equalsIgnoreCase("SUV") && tierIndex == 2 ? "AWD" : "FWD";
		int gc = model.getBodyType().equalsIgnoreCase("SUV") ? 190 + tierIndex * 10 : 165;
		CarVariant v = CarVariant.builder()
				.model(model)
				.variantName(variantName)
				.fuelType(fuel)
				.transmission(transmission)
				.seatingCapacity(seat)
				.engineCc(cc)
				.horsepower(hp)
				.torque(torque)
				.mileage(mileage)
				.cityMileage(mileage.multiply(BigDecimal.valueOf(0.88)).setScale(1, RoundingMode.HALF_UP))
				.highwayMileage(mileage.add(BigDecimal.valueOf(3)))
				.bootSpace(300 + tierIndex * 45)
				.groundClearance(gc)
				.drivetrain(drive)
				.exShowroomPrice(basePrice)
				.onRoadPrice(basePrice.multiply(BigDecimal.valueOf(1.12)).setScale(0, RoundingMode.HALF_UP))
				.imageUrl(pickImage())
				.availabilityStatus(AvailabilityStatus.AVAILABLE)
				.searchCount(ThreadLocalRandom.current().nextLong(50, 5000))
				.viewCount(ThreadLocalRandom.current().nextLong(20, 2000))
				.build();

		v.getSafetyRatings().add(SafetyRating.builder()
				.variant(v)
				.agency("Global NCAP")
				.rating(BigDecimal.valueOf(Math.min(5.0, 3.0 + ThreadLocalRandom.current().nextDouble(0, 2.0)))
						.setScale(1, RoundingMode.HALF_UP))
				.airbags(2 + tierIndex * 2)
				.abs(true)
				.esc(tierIndex >= 1)
				.adasFeatures(tierIndex == 2 ? "Lane assist, adaptive cruise" : "NA")
				.build());

		CarFeature features = CarFeature.builder()
				.variant(v)
				.sunroof(tierIndex >= 1)
				.ventilatedSeats(tierIndex == 2)
				.connectedCarTech(true)
				.infotainmentSize(BigDecimal.valueOf(8 + tierIndex * 2.5))
				.wirelessCharging(tierIndex >= 1)
				.cruiseControl(tierIndex >= 1)
				.panoramicSunroof(tierIndex == 2 && model.getSegment().equalsIgnoreCase("Premium"))
				.audioSystem(tierIndex == 2 ? "Bose premium" : "OEM 6-speaker")
				.build();
		v.setFeatures(features);

		for (int i = 0; i < 3; i++) {
			v.getImages().add(CarImage.builder().variant(v).imageUrl(pickImage()).imageType(i == 0 ? "EXTERIOR" : "INTERIOR").build());
		}

		for (int r = 0; r < 5; r++) {
			v.getReviews().add(buildReview(v, r));
		}
		return v;
	}

	private static UserReview buildReview(CarVariant v, int idx) {
		String[] titles = { "Great overall package", "Value for money", "Comfortable daily driver", "Feature loaded", "Could be better NVH" };
		return UserReview.builder()
				.variant(v)
				.username("owner_" + v.getModel().getModelName().replaceAll("\\s+", "_").toLowerCase() + "_" + idx)
				.rating(BigDecimal.valueOf(3.5 + ThreadLocalRandom.current().nextDouble(0, 1.5)).setScale(1, RoundingMode.HALF_UP))
				.title(titles[idx % titles.length])
				.reviewText("Owned for a few months; " + titles[idx % titles.length].toLowerCase() + " for Indian roads.")
				.pros("Space, features, after-sales")
				.cons("Waiting period, minor rattles")
				.build();
	}

	private static String pickFuel(String modelName, int tier) {
		if (modelName.contains("Hycross") || modelName.contains("Camry")) {
			return "Hybrid";
		}
		return tier == 0 ? "Petrol" : (tier == 1 ? "Petrol" : "Diesel");
	}

	private static BigDecimal basePriceFor(String segment, int tier, boolean ev) {
		long lakh = switch (segment.toLowerCase()) {
			case "mass" -> 6 + tier * 2;
			case "mid" -> 10 + tier * 3;
			case "premium" -> 18 + tier * 4;
			case "luxury" -> 35 + tier * 10;
			default -> 12 + tier * 3;
		};
		if (ev) {
			lakh += 8;
		}
		return BigDecimal.valueOf(lakh * 100_000L);
	}

	private static String pickImage() {
		return IMAGE_POOL[ThreadLocalRandom.current().nextInt(IMAGE_POOL.length)];
	}
}
