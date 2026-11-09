# Stand-Dock — "Living Horizon" OLED Standby Display

A zero-glare, true-black (`#000000`) standby dock designed specifically for OLED phones, tablets, and desk monitors. Over **94% of pixels remain completely off**, allowing active typography, orbital signals, and ambient bezel shaders to float in mid-air.

Repository target: [`https://github.com/nur12islam/Stand-Dock`](https://github.com/nur12islam/Stand-Dock)

## Key Features

### 1. Floating Amber & Obsidian Core Aesthetic
- **True `#000000` Canvas**: Eliminates LCD backlight bleed in dark rooms and minimizes OLED power draw.
- **Circadian Color Shifting**: Switch between **Daylight** (`#00F0FF` cyan & crisp white), **Sunset Phosphor Amber** (`#FF8C00`), **After-Midnight Deep Red** (`#D92626` to preserve night vision), or **AUTO** (syncs automatically with local solar time).
- **Lissajous Micro-Ambient Pixel Drift**: Slowly orbits the entire layout along a mathematical Lissajous curve (`x = A sin(3t), y = B cos(2t)`) to prevent static OLED pixel burn-in. Includes a **15x Drift Preview** toggle.

### 2. 3-Zone Bento Grid
- **Left Zone (Bauhaus Chrono-Dial)**: 60fps sweeping analog second-hand arc surrounding variable-weight digital typography (`12H`/`24H`), **Orbital VIP Ambient Signal Dots**, and a **Waking Day Horizon Progress Bar** (`07:00–23:00`).
- **Center Zone (Next 3-Hour Horizon & Pomodoro "Ember" Timer)**: Interactive 3-hour focus timeline (persisted in `localStorage`) + a glowing coal **Pomodoro Ember Timer** with gentle screen-edge pulse and 528Hz singing-bowl chime on completion.
- **Right Zone (Tactile Audio & Atmosphere)**: Album art with a radial vignette fading seamlessly into `#000000`, a real-time 60fps `<canvas>` waveform visualizer, optional **432Hz Web Audio Ambient Synth Drone**, and live local weather via Open-Meteo.

### 3. Smart & Human-Centric Features
- **Proximity / Radar Wake-Up (`AWAY` / `ROOM` / `NEAR`)**: Supports pointer/touch wake-up and an optional **Webcam Motion Radar** (using local 32×24 frame-diffing in browser memory—zero video leaves your device).
- **Bezel "Shader Weather" Edges**: Subtle particle shaders along the screen borders (`RAIN` cyan droplets, `SOLAR` golden-hour top horizon glow, or `STARS` nocturnal micro-pixels).
- **Dock Lock**: Uses the Fullscreen API and Screen Wake Lock API (`navigator.wakeLock`) so your device never sleeps while docked.

---

## Deploying to GitHub & Cloudflare Pages

### Push to GitHub
```bash
git init
git add .
git commit -m "feat: initial release of Stand-Dock OLED Standby Display"
git branch -M main
git remote add origin https://github.com/nur12islam/Stand-Dock.git
git push -u origin main
```

### Deploy on Cloudflare Pages
1. Log in to the **Cloudflare Dashboard** → **Workers & Pages** → **Create application** → **Pages** → **Connect to Git**.
2. Select `nur12islam/Stand-Dock`.
3. Use these build settings:
   - **Framework preset**: `None` (Static HTML)
   - **Build command**: *(leave empty)*
   - **Build output directory**: `/` (root)
4. Click **Save and Deploy**. Your OLED Standby Dock will be live globally on Cloudflare's edge network within seconds.
