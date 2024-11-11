package com.mgke.kpbrovka;

import com.cloudinary.Cloudinary;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {

    private static Cloudinary cloudinary;

    public static void initCloudinary() {
        if (cloudinary == null) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dicwxping");
            config.put("api_key", "717451445846117");
            config.put("api_secret", "t70lKX8375OLe2DoWfUtU9YW4F4");

            cloudinary = new Cloudinary(config);
        }
    }

    public static Cloudinary getCloudinary() {
        if (cloudinary == null) {
            initCloudinary();
        }
        return cloudinary;
    }
}
