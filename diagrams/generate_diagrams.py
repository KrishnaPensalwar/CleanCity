"""
CleanCityApp – Diagram Generator
Generates 5 clean, GitHub-renderable SVG diagrams.
"""

import os

OUT = os.path.dirname(os.path.abspath(__file__))

# ─── Shared helpers ──────────────────────────────────────────────────────────

FONT = "Arial, Helvetica, sans-serif"

ARROW_DEFS = """
  <defs>
    <marker id="ah-dark" viewBox="0 0 10 10" refX="9" refY="5"
            markerWidth="7" markerHeight="7" orient="auto">
      <polygon points="0,1 10,5 0,9" fill="#455A64"/>
    </marker>
    <marker id="ah-blue" viewBox="0 0 10 10" refX="9" refY="5"
            markerWidth="7" markerHeight="7" orient="auto">
      <polygon points="0,1 10,5 0,9" fill="#1976D2"/>
    </marker>
    <marker id="ah-green" viewBox="0 0 10 10" refX="9" refY="5"
            markerWidth="7" markerHeight="7" orient="auto">
      <polygon points="0,1 10,5 0,9" fill="#388E3C"/>
    </marker>
    <marker id="ah-orange" viewBox="0 0 10 10" refX="9" refY="5"
            markerWidth="7" markerHeight="7" orient="auto">
      <polygon points="0,1 10,5 0,9" fill="#E65100"/>
    </marker>
    <marker id="ah-purple" viewBox="0 0 10 10" refX="9" refY="5"
            markerWidth="7" markerHeight="7" orient="auto">
      <polygon points="0,1 10,5 0,9" fill="#6A1B9A"/>
    </marker>
    <marker id="ah-red" viewBox="0 0 10 10" refX="9" refY="5"
            markerWidth="7" markerHeight="7" orient="auto">
      <polygon points="0,1 10,5 0,9" fill="#C62828"/>
    </marker>
    <filter id="shadow" x="-4%" y="-4%" width="108%" height="108%">
      <feDropShadow dx="2" dy="2" stdDeviation="3" flood-color="#00000018"/>
    </filter>
  </defs>
"""

def svg_open(w, h):
    return (f'<?xml version="1.0" encoding="UTF-8"?>\n'
            f'<svg xmlns="http://www.w3.org/2000/svg" '
            f'viewBox="0 0 {w} {h}" width="{w}" height="{h}">\n'
            f'{ARROW_DEFS}\n'
            f'  <!-- white background -->\n'
            f'  <rect width="{w}" height="{h}" fill="#FFFFFF"/>\n')

def svg_close():
    return '</svg>\n'

def rect(x, y, w, h, fill, stroke, rx=10, sw=2, extra=""):
    return (f'  <rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{rx}" '
            f'fill="{fill}" stroke="{stroke}" stroke-width="{sw}" {extra}/>\n')

def _esc(s):
    return (s.replace("&", "&amp;")
             .replace("<", "&lt;")
             .replace(">", "&gt;")
             .replace('"', "&quot;"))

def text(x, y, s, size=13, weight="normal", fill="#1A1A2E",
         anchor="middle", italic=False):
    style = f'font-style="italic"' if italic else ''
    return (f'  <text x="{x}" y="{y}" font-family="{FONT}" font-size="{size}" '
            f'font-weight="{weight}" fill="{fill}" text-anchor="{anchor}" '
            f'{style}>{_esc(s)}</text>\n')

def line(x1, y1, x2, y2, stroke="#455A64", sw=2, marker="ah-dark", dash=""):
    dash_attr = f'stroke-dasharray="{dash}"' if dash else ""
    return (f'  <line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" '
            f'stroke="{stroke}" stroke-width="{sw}" '
            f'marker-end="url(#{marker})" {dash_attr}/>\n')

def path(d, stroke="#455A64", sw=2, marker="ah-dark", fill="none", dash=""):
    dash_attr = f'stroke-dasharray="{dash}"' if dash else ""
    return (f'  <path d="{d}" stroke="{stroke}" stroke-width="{sw}" '
            f'fill="{fill}" marker-end="url(#{marker})" {dash_attr}/>\n')

def title_bar(w, title, subtitle):
    out = ""
    out += rect(0, 0, w, 72, "#1A237E", "none", rx=0, sw=0)
    out += text(w//2, 38, title, size=26, weight="bold", fill="#FFFFFF")
    out += text(w//2, 60, subtitle, size=13, fill="#90CAF9")
    return out

def section_header(x, y, w, label, fill, stroke, text_fill):
    out = rect(x, y, w, 28, fill, stroke, rx=6, sw=1.5)
    out += text(x + 14, y + 19, label, size=12, weight="bold",
                fill=text_fill, anchor="start")
    return out

def component_box(x, y, w, h, title, lines_text, bg, border, title_fill,
                  text_fill, shadow=True):
    extra = 'filter="url(#shadow)"' if shadow else ""
    out = rect(x, y, w, h, bg, border, rx=8, sw=1.5, extra=extra)
    out += text(x + w//2, y + 22, title, size=12, weight="bold",
                fill=title_fill)
    for i, ln in enumerate(lines_text):
        out += text(x + w//2, y + 38 + i*16, ln, size=10, fill=text_fill)
    return out

# ─── Diagram 1: Architecture ─────────────────────────────────────────────────

def diagram_architecture():
    W, H = 1400, 960
    out = svg_open(W, H)
    out += title_bar(W,
        "CleanCityApp — Architecture Diagram",
        "MVVM  •  Single Activity  •  Jetpack Compose  •  Koin DI  •  Kotlin Coroutines")

    # ── Layer 1: Presentation ──────────────────────────────
    out += rect(30, 88, 1340, 198, "#E3F2FD", "#90CAF9", rx=12, sw=2,
                extra='filter="url(#shadow)"')
    out += text(700, 108, "PRESENTATION LAYER", size=14, weight="bold",
                fill="#0D47A1")
    # divider line inside band
    out += f'  <line x1="30" y1="116" x2="1370" y2="116" stroke="#90CAF9" stroke-width="1"/>\n'

    boxes1 = [
        ("Composable\nScreens", ["LoginScreen", "HomeScreen", "ReportScreen",
                                  "MapScreen · RewardsScreen",
                                  "HistoryScreen · ProfileScreen",
                                  "ComplaintDetailsScreen"]),
        ("ViewModels\n(StateFlow)", ["MainViewModel", "AuthViewModel",
                                     "HomeViewModel · UserViewModel",
                                     "DriverViewModel",
                                     "HistoryVM · ProfileVM",
                                     "RewardsViewModel"]),
        ("Navigation\nCompose", ["NavHost + NavController",
                                  "Screen (route definitions)",
                                  "Back-stack management",
                                  "Deep-link handling",
                                  "Role-based routing",
                                  ""]),
        ("Shared UI\nComponents", ["TopNavBar · BottomNavBar",
                                    "CameraCapture · InputField",
                                    "ThemeSelector · Shimmer",
                                    "ErrorState · Skeletons",
                                    "Role-aware nav bars",
                                    ""]),
        ("State\nManagement", ["StateFlow / collectAsState",
                                "MVI Contracts",
                                "viewModelScope",
                                "Kotlin Coroutines + Flow",
                                "LaunchedEffect / SideEffect",
                                ""]),
        ("UI Theme\n& Styling", ["Material Design 3",
                                  "CleanCityAppTheme",
                                  "Light / Dark / System",
                                  "Custom Color Palette",
                                  "Typography Scale",
                                  ""]),
    ]
    bw, bh, gap = 210, 150, 14
    start_x = 35
    for i, (title_raw, items) in enumerate(boxes1):
        bx = start_x + i * (bw + gap)
        by = 124
        title_parts = title_raw.split("\n")
        out += rect(bx, by, bw, bh, "#BBDEFB", "#42A5F5", rx=8, sw=1.5,
                    extra='filter="url(#shadow)"')
        for tp_i, tp in enumerate(title_parts):
            out += text(bx + bw//2, by + 18 + tp_i*14, tp, size=11,
                        weight="bold", fill="#0D47A1")
        for li, ln in enumerate(items):
            out += text(bx + bw//2, by + 50 + li*16, ln, size=9,
                        fill="#1565C0")

    # Arrow 1→2
    out += line(700, 286, 700, 312, stroke="#1976D2", sw=2.5, marker="ah-blue")

    # ── Layer 2: Data ──────────────────────────────────────
    out += rect(30, 318, 1340, 148, "#E8F5E9", "#A5D6A7", rx=12, sw=2,
                extra='filter="url(#shadow)"')
    out += text(700, 338, "DATA LAYER", size=14, weight="bold", fill="#1B5E20")
    out += f'  <line x1="30" y1="346" x2="1370" y2="346" stroke="#A5D6A7" stroke-width="1"/>\n'

    boxes2 = [
        ("Repositories", ["ComplaintDetailsRepository",
                           "DeviceRegistrationRepository",
                           "Result<T> wrapper pattern",
                           "Suspend functions"]),
        ("Data Models\n/ DTOs", ["LoginResponse · MeResponse",
                                  "ReportResponse · UserDto",
                                  "ComplaintDetailsDto",
                                  "DeviceRegistrationRequest"]),
        ("Presentation\nStates", ["AuthState · HomeState",
                                   "UserState · DriverState",
                                   "HistoryState · RewardsState",
                                   "ProfileState"]),
        ("Local\nPersistence", ["SharedPreferences (auth_prefs)",
                                 "access_token · refresh_token",
                                 "user_role · theme_mode",
                                 "last_registered_fcm_token"]),
        ("Token\nAuthenticator", ["OkHttp Authenticator",
                                   "Auto-refresh on HTTP 401",
                                   "POST /auth/refresh",
                                   "Retry original request"]),
    ]
    bw2, bh2, gap2 = 252, 108, 17
    for i, (title_raw, items) in enumerate(boxes2):
        bx = 35 + i * (bw2 + gap2)
        by = 353
        title_parts = title_raw.split("\n")
        out += rect(bx, by, bw2, bh2, "#C8E6C9", "#66BB6A", rx=8, sw=1.5,
                    extra='filter="url(#shadow)"')
        for tp_i, tp in enumerate(title_parts):
            out += text(bx + bw2//2, by + 18 + tp_i*14, tp, size=11,
                        weight="bold", fill="#1B5E20")
        for li, ln in enumerate(items):
            out += text(bx + bw2//2, by + 50 + li*15, ln, size=9,
                        fill="#2E7D32")

    # Arrow 2→3
    out += line(700, 466, 700, 492, stroke="#388E3C", sw=2.5, marker="ah-green")

    # ── Layer 3: Remote / API ──────────────────────────────
    out += rect(30, 498, 1340, 160, "#FFF8E1", "#FFCC80", rx=12, sw=2,
                extra='filter="url(#shadow)"')
    out += text(700, 518, "REMOTE API LAYER", size=14, weight="bold",
                fill="#E65100")
    out += f'  <line x1="30" y1="526" x2="1370" y2="526" stroke="#FFCC80" stroke-width="1"/>\n'

    boxes3 = [
        ("Retrofit\n(AuthApi)", ["POST /auth/login · /auth/signup",
                                  "POST /auth/refresh",
                                  "GET /auth/me · /api/cities",
                                  "POST /api/reports (multipart)",
                                  "GET /api/users/rank"]),
        ("Ktor HTTP\nClient", ["ComplaintDetailsApi",
                                "GET /api/complaints/{id}",
                                "DriverApi: GET assigned tasks",
                                "DeviceRegistrationApi",
                                "POST/DELETE /api/devices"]),
        ("Firebase", ["Cloud Messaging (FCM)",
                       "MyFirebaseMessagingService",
                       "Push → NotificationHelper",
                       "Firebase Analytics",
                       "BOM 33.9.0"]),
        ("OkHttp\nInterceptors", ["Bearer Token Injection",
                                   "Logging Interceptor",
                                   "TokenAuthenticator hook",
                                   "Connection pooling",
                                   "Timeout config"]),
        ("Image\nLoading (Coil)", ["AsyncImage composable",
                                    "Report photo display",
                                    "Driver completion photos",
                                    "Profile image loading",
                                    "Disk + memory cache"]),
    ]
    bh3 = 118
    for i, (title_raw, items) in enumerate(boxes3):
        bx = 35 + i * (bw2 + gap2)
        by = 533
        title_parts = title_raw.split("\n")
        out += rect(bx, by, bw2, bh3, "#FFE0B2", "#FFA726", rx=8, sw=1.5,
                    extra='filter="url(#shadow)"')
        for tp_i, tp in enumerate(title_parts):
            out += text(bx + bw2//2, by + 18 + tp_i*14, tp, size=11,
                        weight="bold", fill="#E65100")
        for li, ln in enumerate(items):
            out += text(bx + bw2//2, by + 50 + li*15, ln, size=9,
                        fill="#EF6C00")

    # Arrow 3→4
    out += line(700, 658, 700, 684, stroke="#E65100", sw=2.5, marker="ah-orange")

    # ── Layer 4: Backend ───────────────────────────────────
    out += rect(30, 690, 1340, 72, "#FCE4EC", "#F48FB1", rx=12, sw=2,
                extra='filter="url(#shadow)"')
    out += text(700, 714, "BACKEND  —  REST API", size=15, weight="bold",
                fill="#880E4F")
    out += text(700, 734,
                "https://cleancity-backend-au86.onrender.com/   •   "
                "Auth Routes  •  Report Routes  •  Driver Routes  •  "
                "Device Routes  •  Complaint Routes",
                size=11, fill="#AD1457")
    out += text(700, 752,
                "Node.js / Express   •   JWT Authentication   •   "
                "Multipart File Upload   •   MongoDB / PostgreSQL",
                size=10, fill="#C2185B")

    # ── Layer 5: Infrastructure ────────────────────────────
    # Koin DI
    out += rect(30, 778, 655, 100, "#EDE7F6", "#CE93D8", rx=12, sw=2,
                extra='filter="url(#shadow)"')
    out += text(358, 800, "DEPENDENCY INJECTION  —  Koin", size=13,
                weight="bold", fill="#311B92")
    out += text(358, 820,
                "CleanCityApplication → startKoin { appModule }",
                size=10, fill="#4A148C")
    out += text(358, 837,
                "ViewModels via koinViewModel()  •  Retrofit + Ktor + APIs + Repositories",
                size=10, fill="#4A148C")
    out += text(358, 854, "all wired in AppModule.kt", size=10, fill="#4A148C")

    # Notification System
    out += rect(703, 778, 667, 100, "#E8EAF6", "#9FA8DA", rx=12, sw=2,
                extra='filter="url(#shadow)"')
    out += text(1037, 800, "NOTIFICATION SYSTEM  —  Firebase FCM", size=13,
                weight="bold", fill="#1A237E")
    out += text(1037, 820,
                "FCM Token  →  MyFirebaseMessagingService", size=10,
                fill="#283593")
    out += text(1037, 837,
                "→  NotificationHelper  →  Status Notification", size=10,
                fill="#283593")
    out += text(1037, 854,
                "→  MainActivity deep-link  →  ComplaintDetailsScreen", size=10,
                fill="#283593")

    out += svg_close()
    return out


# ─── Diagram 2: Component Diagram ────────────────────────────────────────────

def diagram_components():
    W, H = 1500, 980
    out = svg_open(W, H)
    out += title_bar(W,
        "CleanCityApp — Component Diagram",
        "Major components, their responsibilities, and dependency relationships")

    # Color groups
    APP   = ("#E3F2FD", "#42A5F5", "#0D47A1", "#1565C0")
    PRES  = ("#E8F5E9", "#66BB6A", "#1B5E20", "#2E7D32")
    DATA  = ("#FFF3E0", "#FFA726", "#E65100", "#EF6C00")
    INFRA = ("#EDE7F6", "#AB47BC", "#4A148C", "#6A1B9A")
    NOTIF = ("#E8EAF6", "#5C6BC0", "#1A237E", "#283593")

    def cbox(x, y, w, h, title, items, grp):
        bg, br, tc, ic = grp
        out2 = rect(x, y, w, h, bg, br, rx=8, sw=1.5,
                    extra='filter="url(#shadow)"')
        out2 += text(x + w//2, y + 21, title, size=12, weight="bold",
                     fill=tc)
        out2 += f'  <line x1="{x+8}" y1="{y+28}" x2="{x+w-8}" y2="{y+28}" stroke="{br}" stroke-width="1"/>\n'
        for i, ln in enumerate(items):
            out2 += text(x + w//2, y + 44 + i*16, ln, size=10, fill=ic)
        return out2

    # ── Row 1: App entry points ────────────────────────────
    out += text(750, 96, "APPLICATION ENTRY", size=12, weight="bold",
                fill="#37474F")
    # CleanCityApplication
    out += cbox(120, 108, 300, 110, "CleanCityApplication",
                ["extends Application",
                 "startKoin { appModule }",
                 "Firebase initialization",
                 "App lifecycle owner"], APP)
    # MainActivity
    out += cbox(540, 108, 300, 110, "MainActivity",
                ["Single ComponentActivity",
                 "Edge-to-edge Compose host",
                 "FCM deep-link handling",
                 "Notification permission"], APP)
    # MainApp
    out += cbox(960, 108, 300, 110, "MainApp (Composable)",
                ["Root Scaffold",
                 "NavHost + NavController",
                 "TopNavBar + BottomNavBar",
                 "FAB + MainViewModel"], APP)

    # Arrow App → Main
    out += line(420, 163, 540, 163, stroke="#1976D2", sw=2, marker="ah-blue")
    out += line(840, 163, 960, 163, stroke="#1976D2", sw=2, marker="ah-blue")

    # ── Row 2: Presentation screens ───────────────────────
    out += text(750, 256, "PRESENTATION LAYER  —  Screens & ViewModels", size=12,
                weight="bold", fill="#37474F")

    screens_data = [
        ("Auth Screens", ["LoginScreen", "SignUpScreen", "RoleSelectionScreen",
                           "AuthViewModel"]),
        ("Home / User", ["HomeScreen + HomeViewModel",
                          "ReportScreen + UserViewModel",
                          "MapScreen (static UI)",
                          "RewardsScreen + RewardsVM"]),
        ("History &\nDetails", ["HistoryScreen + HistoryVM",
                                 "ReportDetailsScreen",
                                 "ComplaintDetailsScreen",
                                 "ComplaintDetailsVM"]),
        ("Profile", ["ProfileScreen + ProfileVM",
                      "DriverProfileScreen",
                      "ThemeMode selection",
                      "Logout flow"]),
        ("Driver\nScreens", ["DriverDashboardScreen",
                               "DriverTasksScreen",
                               "DriverRouteScreen",
                               "DriverViewModel"]),
        ("Shared UI\nComponents", ["BottomNavBar · TopNavBar",
                                    "CameraCapture · InputField",
                                    "Shimmer · Skeletons",
                                    "ErrorState"]),
    ]
    s_w, s_h, s_gap = 225, 108, 15
    s_start = 30
    for i, (title_raw, items) in enumerate(screens_data):
        sx = s_start + i * (s_w + s_gap)
        out += cbox(sx, 268, s_w, s_h, title_raw, items, PRES)
        # arrow from MainApp down to each screen group
        from_x = 1110
        if i < 3:
            out += path(
                f"M {from_x} 218 Q {sx + s_w//2} 240 {sx + s_w//2} 268",
                stroke="#66BB6A", sw=1.5, marker="ah-green")
        else:
            out += path(
                f"M {from_x} 218 Q {sx + s_w//2} 240 {sx + s_w//2} 268",
                stroke="#66BB6A", sw=1.5, marker="ah-green")

    # ── Row 3: Data layer ──────────────────────────────────
    out += text(750, 422, "DATA LAYER  —  Repositories, Models & Local Storage",
                size=12, weight="bold", fill="#37474F")

    data_boxes = [
        ("ComplaintDetails\nRepository", ["Wraps ComplaintDetailsApi",
                                           "Returns Result<T>",
                                           "Suspend function pattern",
                                           "Error mapping"]),
        ("DeviceRegistration\nRepository", ["FCM token registration",
                                             "Duplicate-token guard",
                                             "Unregister on logout",
                                             "Ktor-based calls"]),
        ("Auth Models\n(DTOs)", ["LoginResponse / MeResponse",
                                  "ReportResponse / UserDto",
                                  "RankResponse / CityDto",
                                  "@Serializable / @SerialName"]),
        ("SharedPreferences\n(auth_prefs)", ["access_token / refresh_token",
                                              "user_role (USER / DRIVER)",
                                              "theme_mode",
                                              "last_registered_fcm_token"]),
        ("TokenAuthenticator", ["OkHttp Authenticator",
                                 "Intercepts HTTP 401 errors",
                                 "Calls /auth/refresh silently",
                                 "Retries original request"]),
    ]
    d_w, d_h, d_gap = 268, 108, 15
    d_start = 30
    for i, (title_raw, items) in enumerate(data_boxes):
        dx = d_start + i * (d_w + d_gap)
        out += cbox(dx, 435, d_w, d_h, title_raw, items, DATA)

    # Arrows pres → data
    pres_centers = [s_start + i*(s_w+s_gap) + s_w//2 for i in range(6)]
    data_centers = [d_start + i*(d_w+d_gap) + d_w//2 for i in range(5)]
    for pc in pres_centers[:5]:
        nearest_dc = min(data_centers, key=lambda dc: abs(dc-pc))
        out += line(pc, 376, nearest_dc, 435, stroke="#FFA726", sw=1.5,
                    marker="ah-orange")

    # ── Row 4: Remote APIs ─────────────────────────────────
    out += text(750, 590, "REMOTE API LAYER  —  Network Clients",
                size=12, weight="bold", fill="#37474F")

    api_boxes = [
        ("AuthApi\n(Retrofit)", ["Retrofit + OkHttp",
                                  "Auth, Reports, Rank",
                                  "Multipart uploads",
                                  "Gson / kotlinx.serial"]),
        ("DriverApi\n(Ktor)", ["GET assigned reports",
                                "Ktor HttpClient",
                                "kotlinx.serialization",
                                "Bearer auth header"]),
        ("ComplaintDetails\nApi (Ktor)", ["GET /api/complaints/{id}",
                                           "Ktor HttpClient",
                                           "ComplaintDetailsDto",
                                           "JSON deserialization"]),
        ("DeviceRegistration\nApi (Ktor)", ["POST /api/devices/register",
                                             "DELETE /api/devices/{id}",
                                             "FCM token push",
                                             "Ktor HttpClient"]),
        ("Firebase\nSDK", ["FCM messaging",
                            "Token management",
                            "Analytics events",
                            "BOM 33.9.0"]),
    ]
    a_w, a_h, a_gap = 268, 108, 15
    a_start = 30
    for i, (title_raw, items) in enumerate(api_boxes):
        ax = a_start + i * (a_w + a_gap)
        out += cbox(ax, 602, a_w, a_h, title_raw, items, NOTIF)

    # Arrows data → api
    for di, dc in enumerate(data_centers):
        api_dc = a_start + di*(a_w+a_gap) + a_w//2
        out += line(dc, 543, api_dc, 602, stroke="#5C6BC0", sw=1.5,
                    marker="ah-dark")

    # ── Row 5: DI & Infra ──────────────────────────────────
    out += text(750, 756, "INFRASTRUCTURE  —  Dependency Injection & Services",
                size=12, weight="bold", fill="#37474F")

    infra_boxes = [
        ("AppModule\n(Koin)", ["Single Koin module",
                                "Provides Retrofit + Ktor",
                                "Provides all APIs",
                                "Provides all ViewModels",
                                "Provides Repositories"]),
        ("NetworkModule\n(Koin – unused)", ["Defined but not loaded",
                                             "Duplicate of AppModule",
                                             "Could be activated",
                                             "network layer config",
                                             ""]),
        ("MyFirebase\nMessagingService", ["Extends FirebaseMsg…Service",
                                           "onNewToken → register device",
                                           "onMessageReceived",
                                           "Notification display",
                                           "Deep-link construction"]),
        ("NotificationHelper", ["createNotificationChannel",
                                  "Complaint status notifications",
                                  "PendingIntent → MainActivity",
                                  "Action buttons in notif.",
                                  "Android 13+ compat"]),
        ("DeviceInfoUtils", ["getAndroidId()",
                               "getDeviceName()",
                               "Used for FCM registration",
                               "Device fingerprinting",
                               ""]),
    ]
    i_w, i_h, i_gap = 268, 118, 15
    i_start = 30
    for i, (title_raw, items) in enumerate(infra_boxes):
        ix = i_start + i * (i_w + i_gap)
        out += cbox(ix, 770, i_w, i_h, title_raw, items, INFRA)

    # backend box at bottom
    out += rect(30, 908, 1440, 50, "#FCE4EC", "#F48FB1", rx=10, sw=2)
    out += text(750, 929, "BACKEND  —  https://cleancity-backend-au86.onrender.com/",
                size=13, weight="bold", fill="#880E4F")
    out += text(750, 947,
                "REST API  •  JWT Auth  •  Report Management  •  "
                "Driver Task Assignment  •  Device Registry",
                size=10, fill="#AD1457")

    out += svg_close()
    return out


# ─── Diagram 3: UI Hierarchy ──────────────────────────────────────────────────

def diagram_ui_hierarchy():
    W, H = 1500, 1060
    out = svg_open(W, H)
    out += title_bar(W,
        "CleanCityApp — UI Hierarchy / Screen Structure",
        "Complete Composable tree from MainActivity down to leaf-level UI components")

    LINE_CLR = "#90A4AE"
    sw = 1.5

    def node(x, y, w, h, label, sub, fill, stroke, tc, sc, corner=8):
        out2 = rect(x, y, w, h, fill, stroke, rx=corner, sw=1.5,
                    extra='filter="url(#shadow)"')
        lines = label.split("\n")
        base_y = y + (h - len(lines)*16 - (len(sub)*14 if sub else 0)) // 2 + 14
        for i, l in enumerate(lines):
            out2 += text(x + w//2, base_y + i*16, l, size=11,
                         weight="bold", fill=tc)
        base_y2 = base_y + len(lines)*16 + 2
        for i, s in enumerate(sub or []):
            out2 += text(x + w//2, base_y2 + i*14, s, size=9, fill=sc)
        return out2

    def connector(x1, y1, x2, y2):
        mx = (x1 + x2) // 2
        my = (y1 + y2) // 2
        return (f'  <line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" '
                f'stroke="{LINE_CLR}" stroke-width="{sw}" '
                f'marker-end="url(#ah-dark)"/>\n')

    def h_line(x1, y, x2):
        return (f'  <line x1="{x1}" y1="{y}" x2="{x2}" y2="{y}" '
                f'stroke="{LINE_CLR}" stroke-width="{sw}"/>\n')

    def v_line(x, y1, y2):
        return (f'  <line x1="{x}" y1="{y1}" x2="{x}" y2="{y2}" '
                f'stroke="{LINE_CLR}" stroke-width="{sw}"/>\n')

    # Root
    out += node(620, 88, 260, 60, "MainActivity",
                ["ComponentActivity", "Edge-to-edge", "Deep-link host"],
                "#1A237E", "#1A237E", "#FFFFFF", "#90CAF9", corner=30)

    out += connector(750, 148, 750, 178)

    # MainApp
    out += node(560, 178, 380, 70, "MainApp  (Composable)",
                ["Scaffold + NavHost", "TopNavBar + BottomNavBar + FAB"],
                "#283593", "#3F51B5", "#FFFFFF", "#9FA8DA")

    # Branch down to NavHost + SharedUI
    out += v_line(750, 248, 278)
    out += h_line(400, 278, 1100)
    out += v_line(400, 278, 308)
    out += v_line(750, 278, 308)
    out += v_line(1100, 278, 308)

    # Shared UI Components (left branch)
    out += node(280, 308, 240, 80, "Shared Components",
                ["TopNavBar", "BottomNavBar (role-aware)", "FAB (Report shortcut)",
                 "ThemeSelector"],
                "#E8EAF6", "#5C6BC0", "#1A237E", "#3F51B5")

    # NavHost (center)
    out += node(630, 308, 240, 60, "NavHost",
                ["Jetpack Navigation Compose", "Screen route definitions"],
                "#37474F", "#546E7A", "#FFFFFF", "#B0BEC5")

    # UI Theme (right branch)
    out += node(980, 308, 240, 80, "CleanCityAppTheme",
                ["Material Design 3", "Light / Dark / System",
                 "Color · Typography", "Dynamic theming"],
                "#E8EAF6", "#5C6BC0", "#1A237E", "#3F51B5")

    out += connector(750, 368, 750, 398)
    # NavHost branches down
    out += v_line(750, 368, 398)
    out += h_line(240, 398, 1260)

    # Three main branches: Auth, User, Driver
    auth_cx = 320
    user_cx = 750
    drv_cx  = 1200

    for cx in [auth_cx, user_cx, drv_cx]:
        out += v_line(cx, 398, 428)

    # Auth Branch
    out += node(200, 428, 240, 54, "Auth Flow",
                ["login · signup · role_selection"],
                "#FFF8E1", "#FFB300", "#E65100", "#EF6C00")
    out += v_line(auth_cx, 482, 512)
    auth_screens = ["LoginScreen", "SignUpScreen", "RoleSelectionScreen"]
    for i, s in enumerate(auth_screens):
        sx = 120 + i * 120
        sy = 512
        out += node(sx, sy, 110, 46, s, [], "#FFF3E0", "#FFA726", "#BF360C",
                    "#BF360C", corner=6)
        out += v_line(sx + 55, 482, sy)
        out += h_line(175, 482, 345)

    # User Branch
    out += node(600, 428, 300, 54, "User Flow  (citizen)",
                ["home · report · map · rewards · history · profile"],
                "#E8F5E9", "#4CAF50", "#1B5E20", "#2E7D32")
    out += v_line(user_cx, 482, 512)

    user_screens = [
        ("HomeScreen", ["StatBubble", "MapCard", "ActivityItem"]),
        ("ReportScreen", ["CameraCapture", "ReportCategorySelector"]),
        ("MapScreen", ["MapHeatmapCard", "MapActivityItem", "LegendItem"]),
        ("RewardsScreen", ["RewardsLeaderboard", "LeaderboardRow"]),
        ("HistoryScreen", ["HistoryFilterBar", "ReportHistoryCard"]),
        ("ProfileScreen", ["StatBubble", "ThemeSelectorRow", "ProfileSettingRow"]),
        ("ComplaintDetails\nScreen", ["ComplaintDetailsVM", "MVI Contracts"]),
        ("ReportDetails\nScreen", ["Report info", "Image viewer"]),
    ]
    uw = 160
    u_total = len(user_screens) * uw + (len(user_screens)-1) * 8
    u_start = user_cx - u_total//2
    out += h_line(u_start + uw//2, 512,
                  u_start + (len(user_screens)-1)*(uw+8) + uw//2)
    for i, (sname, subs) in enumerate(user_screens):
        sx = u_start + i*(uw+8)
        out += v_line(sx + uw//2, 512, 542)
        sh = 54 + len(subs)*14
        out += node(sx, 542, uw, sh, sname, subs,
                    "#DCEDC8", "#8BC34A", "#1B5E20", "#33691E", corner=6)

    # Driver Branch
    out += node(1080, 428, 240, 54, "Driver Flow",
                ["driver_dashboard · tasks · route · profile"],
                "#E3F2FD", "#1976D2", "#0D47A1", "#1565C0")
    out += v_line(drv_cx, 482, 512)

    drv_screens = [
        ("DriverDashboard\nScreen", ["DriverDutyStatusRow",
                                      "DashboardStatsSection",
                                      "ProgressCard",
                                      "NextTaskSection",
                                      "QuickActions"]),
        ("DriverTasks\nScreen", ["DriverTaskFilterChip",
                                  "DriverTaskCard",
                                  "Camera flow"]),
        ("DriverRoute\nScreen", ["Task details", "CameraCapture",
                                  "Completion upload"]),
        ("DriverProfile\nScreen", ["Profile info", "Settings",
                                    "Logout"]),
        ("History Screen\n(shared)", ["HistoryFilterBar",
                                       "ReportHistoryCard",
                                       "isDriver=true"]),
    ]
    dw = 185
    d_total = len(drv_screens) * dw + (len(drv_screens)-1) * 8
    d_start = drv_cx - d_total//2
    out += h_line(d_start + dw//2, 512,
                  d_start + (len(drv_screens)-1)*(dw+8) + dw//2)
    for i, (sname, subs) in enumerate(drv_screens):
        sx = d_start + i*(dw+8)
        out += v_line(sx + dw//2, 512, 542)
        sh = 54 + len(subs)*14
        out += node(sx, 542, dw, sh, sname, subs,
                    "#BBDEFB", "#42A5F5", "#0D47A1", "#1565C0", corner=6)

    # Legend
    legend_y = H - 60
    out += rect(30, legend_y, 1440, 42, "#F5F5F5", "#E0E0E0", rx=8, sw=1)
    colors = [("#1A237E","#FFFFFF","MainActivity / MainApp"),
              ("#37474F","#FFFFFF","NavHost"),
              ("#FFF3E0","#E65100","Auth Screens"),
              ("#E8F5E9","#1B5E20","User Screens"),
              ("#E3F2FD","#0D47A1","Driver Screens"),
              ("#E8EAF6","#1A237E","Shared Components")]
    for i, (bg, tc, label) in enumerate(colors):
        lx = 50 + i * 240
        out += rect(lx, legend_y + 8, 14, 14, bg, "#90A4AE", rx=3, sw=1)
        out += text(lx + 20, legend_y + 20, label, size=10, fill="#37474F",
                    anchor="start")

    out += svg_close()
    return out


# ─── Diagram 4: Navigation Flow ───────────────────────────────────────────────

def diagram_navigation():
    W, H = 1500, 980
    out = svg_open(W, H)
    out += title_bar(W,
        "CleanCityApp — Navigation / User Flow Diagram",
        "Screen transitions, auth gating, deep-links, and role-based routing")

    def screen_box(x, y, w, h, label, sub, fill, stroke, tc, sc):
        e = 'filter="url(#shadow)"'
        o = rect(x, y, w, h, fill, stroke, rx=10, sw=2, extra=e)
        lines = label.split("\n")
        base = y + (h - len(lines)*18) // 2 + 14
        for i, l in enumerate(lines):
            o += text(x + w//2, base + i*18, l, size=12, weight="bold",
                      fill=tc)
        if sub:
            o += text(x + w//2, base + len(lines)*18 + 2, sub, size=9,
                      fill=sc)
        return o

    def diamond(x, y, w, h, label, fill, stroke, tc):
        cx, cy = x + w//2, y + h//2
        pts = f"{cx},{y} {x+w},{cy} {cx},{y+h} {x},{cy}"
        o = f'  <polygon points="{pts}" fill="{fill}" stroke="{stroke}" stroke-width="2" filter="url(#shadow)"/>\n'
        lines = label.split("\n")
        for i, l in enumerate(lines):
            o += text(cx, cy - (len(lines)-1)*8 + i*16, l, size=10,
                      weight="bold", fill=tc)
        return o

    def arr(x1, y1, x2, y2, label="", clr="#546E7A", mk="ah-dark",
            dsh="", bend=0):
        if bend != 0:
            mx = (x1+x2)//2 + bend
            my = (y1+y2)//2
            d = f"M {x1} {y1} Q {mx} {my} {x2} {y2}"
            o = path(d, stroke=clr, sw=2, marker=mk, fill="none", dash=dsh)
        else:
            o = line(x1, y1, x2, y2, stroke=clr, sw=2, marker=mk, dash=dsh)
        if label:
            lx = (x1+x2)//2 + (bend//3 if bend else 0)
            ly = (y1+y2)//2 - 7
            o += text(lx, ly, label, size=9, fill=clr)
        return o

    # ── App Start ──────────────────────────────────────────
    out += rect(630, 88, 240, 50, "#263238", "#37474F", rx=25, sw=2,
                extra='filter="url(#shadow)"')
    out += text(750, 118, "App Launch", size=14, weight="bold", fill="#FFFFFF")

    out += arr(750, 138, 750, 172)

    # Check session
    out += diamond(660, 172, 180, 60, "Saved\nSession?",
                   "#FFF9C4", "#F9A825", "#E65100")
    # Yes → saved role?
    out += arr(840, 202, 910, 202, "YES", clr="#388E3C", mk="ah-green")
    out += diamond(910, 172, 200, 60, "Saved\nRole?",
                   "#FFF9C4", "#F9A825", "#E65100")
    # No → login
    out += arr(750, 232, 750, 262, "NO", clr="#C62828", mk="ah-red")

    # Login Screen
    out += screen_box(630, 262, 240, 64, "LoginScreen",
                      "POST /auth/login",
                      "#FCE4EC", "#E91E63", "#880E4F", "#C2185B")
    # Sign Up
    out += screen_box(380, 262, 200, 64, "SignUpScreen",
                      "POST /auth/signup",
                      "#FCE4EC", "#E91E63", "#880E4F", "#C2185B")
    out += arr(630, 294, 580, 294, "Sign Up", clr="#E91E63", mk="ah-red", bend=0)
    out += arr(580, 326, 630, 326, "Back", clr="#90A4AE", mk="ah-dark",
               dsh="5,4")

    # Role selection (when multiple roles, no saved pref)
    out += arr(1010, 202, 1110, 202, "NO pref", clr="#F57C00", mk="ah-orange")
    out += screen_box(1110, 172, 220, 60, "RoleSelection\nScreen",
                      "Choose USER or DRIVER",
                      "#FFF3E0", "#FF9800", "#E65100", "#EF6C00")

    # From login → role check
    out += arr(750, 326, 750, 358)
    out += diamond(660, 358, 180, 60, "User\nRole?",
                   "#E3F2FD", "#1976D2", "#0D47A1")

    # ── User flow ──────────────────────────────────────────
    out += arr(660, 388, 410, 438, "USER", clr="#1976D2", mk="ah-blue")
    out += screen_box(290, 438, 240, 64, "HomeScreen",
                      "citizen dashboard",
                      "#E8F5E9", "#4CAF50", "#1B5E20", "#2E7D32")

    # User bottom nav
    user_screens_nav = [
        (130, 545, "Report\nScreen", "POST /api/reports"),
        (310, 545, "Map\nScreen",    "Nearby activity"),
        (490, 545, "Rewards\nScreen","GET /api/users/rank"),
    ]
    for bx, by, bname, bsub in user_screens_nav:
        out += screen_box(bx, by, 168, 64, bname, bsub,
                          "#DCEDC8", "#8BC34A", "#1B5E20", "#33691E")
        cx_src = 410
        cx_dst = bx + 84
        out += path(
            f"M {cx_src} 502 Q {(cx_src+cx_dst)//2} 525 {cx_dst} 545",
            stroke="#66BB6A", sw=1.5, marker="ah-green")

    # History screen
    out += screen_box(490, 438, 200, 64, "HistoryScreen",
                      "GET /api/reports/me",
                      "#DCEDC8", "#8BC34A", "#1B5E20", "#33691E")
    out += path(f"M 410 502 Q 540 525 590 502", stroke="#66BB6A", sw=1.5,
                marker="ah-green")
    # History → Report Details
    out += screen_box(490, 545, 200, 64, "ReportDetails\nScreen",
                      "inline detail view",
                      "#F1F8E9", "#AED581", "#33691E", "#558B2F")
    out += arr(590, 502, 590, 545, clr="#66BB6A", mk="ah-green")

    # Profile
    out += screen_box(290, 650, 200, 64, "ProfileScreen",
                      "GET /auth/me  •  logout",
                      "#DCEDC8", "#8BC34A", "#1B5E20", "#33691E")
    out += path(f"M 410 438 Q 390 560 390 650",
                stroke="#66BB6A", sw=1.5, marker="ah-green", fill="none")

    # ComplaintDetails (deep link)
    out += screen_box(700, 650, 230, 64, "ComplaintDetails\nScreen",
                      "GET /api/complaints/{id}",
                      "#E8EAF6", "#3F51B5", "#1A237E", "#283593")
    out += arr(880, 680, 930, 680, "FCM Deep Link", clr="#5C6BC0", mk="ah-dark",
               dsh="6,4")
    out += rect(930, 658, 180, 44, "#FFF3E0", "#FF9800", rx=8, sw=1.5)
    out += text(1020, 676, "FCM Push", size=11, weight="bold", fill="#E65100")
    out += text(1020, 692, "Notification Tap", size=9, fill="#EF6C00")

    # ── Driver flow ────────────────────────────────────────
    out += arr(840, 388, 1100, 438, "DRIVER", clr="#42A5F5", mk="ah-blue")
    out += screen_box(980, 438, 240, 64, "DriverDashboard\nScreen",
                      "overview + quick actions",
                      "#E3F2FD", "#1976D2", "#0D47A1", "#1565C0")

    drv_nav = [
        (860, 545, "DriverTasks\nScreen", "GET assigned tasks"),
        (1040, 545, "DriverRoute\nScreen", "task route + photo"),
        (1220, 545, "DriverProfile\nScreen", "profile + settings"),
    ]
    for bx, by, bname, bsub in drv_nav:
        out += screen_box(bx, by, 178, 64, bname, bsub,
                          "#BBDEFB", "#42A5F5", "#0D47A1", "#1565C0")
        cx_src2 = 1100
        cx_dst2 = bx + 89
        out += path(
            f"M {cx_src2} 502 Q {(cx_src2+cx_dst2)//2} 525 {cx_dst2} 545",
            stroke="#1976D2", sw=1.5, marker="ah-blue")

    # DriverTasks → camera upload
    out += screen_box(860, 648, 178, 58, "Camera /\nPhoto Upload",
                      "multipart POST",
                      "#E3F2FD", "#90CAF9", "#1A237E", "#283593")
    out += arr(949, 609, 949, 648, clr="#1976D2", mk="ah-blue")

    # DriverRoute → completion
    out += screen_box(1040, 648, 178, 58, "Completion\nPhoto Upload",
                      "PATCH completion-photo",
                      "#E3F2FD", "#90CAF9", "#1A237E", "#283593")
    out += arr(1129, 609, 1129, 648, clr="#1976D2", mk="ah-blue")

    # Saved role YES → goto appropriate home
    out += path(f"M 1010 202 Q 1050 250 1010 280 Q 980 320 840 388",
                stroke="#388E3C", sw=1.5, marker="ah-green", fill="none")

    # Legend
    ly = H - 62
    out += rect(30, ly, 1440, 44, "#F5F5F5", "#E0E0E0", rx=8, sw=1)
    items = [
        ("#263238","#FFFFFF","Entry / Decision"),
        ("#FCE4EC","#880E4F","Auth Screens"),
        ("#E8F5E9","#1B5E20","User Screens"),
        ("#E3F2FD","#0D47A1","Driver Screens"),
        ("#FFF9C4","#E65100","Decision Diamond"),
        ("#E8EAF6","#1A237E","System / Deep-link"),
    ]
    for i, (bg, tc, label) in enumerate(items):
        lx = 50 + i * 245
        out += rect(lx, ly+10, 14, 14, bg, "#90A4AE", rx=3, sw=1)
        out += text(lx+20, ly+22, label, size=10, fill="#37474F",
                    anchor="start")

    out += svg_close()
    return out


# ─── Diagram 5: Data Flow / State Management ─────────────────────────────────

def diagram_dataflow():
    W, H = 1200, 1020
    out = svg_open(W, H)
    out += title_bar(W,
        "CleanCityApp — Data Flow / State Management Diagram",
        "MVVM reactive data flow: UI → ViewModel → Repository → API → Backend and back")

    def block(x, y, w, h, title, items, fill, stroke, tc, ic, corner=10):
        e = 'filter="url(#shadow)"'
        o = rect(x, y, w, h, fill, stroke, rx=corner, sw=2, extra=e)
        o += text(x + w//2, y + 22, title, size=13, weight="bold", fill=tc)
        o += f'  <line x1="{x+10}" y1="{y+30}" x2="{x+w-10}" y2="{y+30}" stroke="{stroke}" stroke-width="1"/>\n'
        for i, ln in enumerate(items):
            o += text(x + w//2, y + 46 + i*17, ln, size=10, fill=ic)
        return o

    def side_block(x, y, w, h, title, items, fill, stroke, tc, ic):
        o = block(x, y, w, h, title, items, fill, stroke, tc, ic)
        return o

    # Main column
    cx = 600       # center x of main column
    bw = 560       # block width
    bx = cx - bw//2

    BLOCKS = [
        # (y, height, title, items, fill, stroke, tc, ic)
        (88, 100, "User Interaction  (Compose UI)",
         ["User taps button / fills form / triggers event",
          "@Composable screens observe StateFlow via collectAsState()",
          "LaunchedEffect for side effects (e.g. navigate on success)",
          "UI re-composes only on state change (smart recomposition)"],
         "#E3F2FD", "#1976D2", "#0D47A1", "#1565C0"),

        (258, 110, "ViewModel  (StateFlow + Coroutines)",
         ["Holds _uiState: MutableStateFlow<State>",
          "Exposes uiState: StateFlow<State> (read-only)",
          "viewModelScope.launch { } for async work",
          "MVI pattern: Intent → reduce → emit new State",
          "Survives configuration changes (AAC ViewModel)"],
         "#E8F5E9", "#4CAF50", "#1B5E20", "#2E7D32"),

        (438, 90, "Repository  (optional abstraction)",
         ["Thin wrapper over API client",
          "Returns Result<T> (success / failure)",
          "ComplaintDetailsRepository · DeviceRegistrationRepository",
          "Hides API implementation details from ViewModel"],
         "#FFF8E1", "#FFB300", "#E65100", "#EF6C00"),

        (598, 100, "API Client  (Retrofit / Ktor)",
         ["Retrofit — AuthApi: auth, reports, rank (JSON + multipart)",
          "Ktor — DriverApi, ComplaintDetailsApi, DeviceRegistrationApi",
          "OkHttp interceptors inject Bearer token from SharedPreferences",
          "TokenAuthenticator catches 401 → refreshes → retries request"],
         "#FFF3E0", "#FF9800", "#BF360C", "#E64A19"),

        (768, 90, "Network Layer  (OkHttp)",
         ["HTTPS request with Authorization: Bearer <token>",
          "Logging interceptor for debug builds",
          "TokenAuthenticator: POST /auth/refresh on 401",
          "Gson / kotlinx.serialization for JSON parsing"],
         "#FCE4EC", "#E91E63", "#880E4F", "#C2185B"),

        (928, 60, "Backend  —  https://cleancity-backend-au86.onrender.com/",
         ["REST API: Auth · Reports · Driver Tasks · Devices · Complaints"],
         "#263238", "#37474F", "#FFFFFF", "#B0BEC5"),
    ]

    block_midpoints = []
    for (by2, bh, title, items, fill, stroke, tc, ic) in BLOCKS:
        out += block(bx, by2, bw, bh, title, items, fill, stroke, tc, ic)
        block_midpoints.append((by2, bh))

    # Downward arrows (request flow)
    for i in range(len(block_midpoints)-1):
        y_bottom = block_midpoints[i][0] + block_midpoints[i][1]
        y_top    = block_midpoints[i+1][0]
        mid_y    = (y_bottom + y_top) // 2
        ax = cx - 60
        out += line(ax, y_bottom, ax, y_top,
                    stroke="#455A64", sw=2.5, marker="ah-dark")
        req_labels = ["collectAsState() ↓ events",
                      "suspend fun call",
                      "API method call",
                      "HTTP request",
                      "TCP/HTTPS"]
        out += text(ax - 10, mid_y, req_labels[i], size=9, fill="#455A64",
                    anchor="end")

    # Upward arrows (response / state update flow)
    for i in range(len(block_midpoints)-1, 0, -1):
        y_top    = block_midpoints[i-1][0] + block_midpoints[i-1][1]
        y_bottom = block_midpoints[i][0]
        mid_y    = (y_top + y_bottom) // 2
        ax = cx + 60
        out += line(ax, y_bottom, ax, y_top,
                    stroke="#388E3C", sw=2.5, marker="ah-green")
        resp_labels = ["emit new State →\ncollectAsState re-renders",
                       "Result<T>",
                       "deserialized response",
                       "HTTP response body",
                       "JSON data"]
        label = resp_labels[len(block_midpoints)-1-i]
        out += text(ax + 10, mid_y, label, size=9, fill="#388E3C",
                    anchor="start")

    # ── Side panel: SharedPreferences ─────────────────────
    sp_x = bx + bw + 50
    sp_y = 550
    out += side_block(sp_x, sp_y, 260, 140, "SharedPreferences",
                      ["auth_prefs",
                       "access_token (JWT)",
                       "refresh_token (JWT)",
                       "user_role (USER/DRIVER)",
                       "theme_mode",
                       "last_registered_fcm_token"],
                      "#EDE7F6", "#AB47BC", "#4A148C", "#6A1B9A")
    # Arrow: API → SharedPreferences (read token)
    out += path(
        f"M {bx + bw} 665 Q {sp_x - 20} 660 {sp_x} 620",
        stroke="#AB47BC", sw=1.5, marker="ah-purple", fill="none")
    out += text(bx + bw + 10, 648, "read token", size=9, fill="#6A1B9A",
                anchor="start")
    out += path(
        f"M {sp_x} 680 Q {sp_x - 20} 700 {bx + bw} 700",
        stroke="#AB47BC", sw=1.5, marker="ah-purple", fill="none",
        dash="5,4")
    out += text(bx + bw + 10, 706, "write refresh", size=9, fill="#6A1B9A",
                anchor="start")

    # ── Side panel: FCM notification flow ─────────────────
    fcm_x = bx - 300
    fcm_y = 368
    out += side_block(fcm_x, fcm_y, 260, 200, "FCM Notification Flow",
                      ["Firebase sends push message",
                       "MyFirebaseMessagingService",
                       "  onMessageReceived()",
                       "NotificationHelper",
                       "  createNotificationChannel()",
                       "  show status notification",
                       "User taps → MainActivity",
                       "  deep-link → complaint/{id}",
                       "ComplaintDetailsScreen"],
                      "#E8EAF6", "#5C6BC0", "#1A237E", "#3F51B5")
    # Arrow from backend to FCM side
    out += path(
        f"M {bx} 985 Q {fcm_x + 260} 950 {fcm_x + 260} 568",
        stroke="#5C6BC0", sw=1.5, marker="ah-dark", fill="none", dash="5,4")
    out += text(fcm_x + 262, 900, "FCM push →", size=9, fill="#3F51B5",
                anchor="start")

    # Arrow from FCM to UI
    out += path(
        f"M {fcm_x + 260} 368 Q {fcm_x + 300} 160 {bx} 140",
        stroke="#5C6BC0", sw=1.5, marker="ah-dark", fill="none", dash="5,4")
    out += text(fcm_x + 262, 250, "← navigate to\nComplaintDetails", size=9,
                fill="#3F51B5", anchor="start")

    # ── Legend ─────────────────────────────────────────────
    # (no extra legend needed, colors are self-explanatory)
    out += rect(30, H-45, W-60, 28, "#F5F5F5", "#E0E0E0", rx=6, sw=1)
    out += text(W//2, H-26,
                "Downward arrows (dark) = request / data fetch     "
                "Upward arrows (green) = response / state update     "
                "Dashed arrows = async / side-channel flow",
                size=9, fill="#546E7A")

    out += svg_close()
    return out


# ─── Write all files ─────────────────────────────────────────────────────────

diagrams = [
    ("01_architecture_diagram.svg", diagram_architecture),
    ("02_component_diagram.svg",    diagram_components),
    ("03_ui_hierarchy.svg",         diagram_ui_hierarchy),
    ("04_navigation_flow.svg",      diagram_navigation),
    ("05_data_flow.svg",            diagram_dataflow),
]

for filename, fn in diagrams:
    content = fn()
    filepath = os.path.join(OUT, filename)
    with open(filepath, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"✓  {filename}")

# ── README.md ──────────────────────────────────────────────────────────────
readme = """# CleanCityApp — Architecture Diagrams

Five architecture diagrams for the **CleanCityApp** Android project.
All diagrams are rendered as SVG (GitHub renders them natively).

---

## 1. Architecture Diagram

Overview of the MVVM layered architecture — Presentation → Data → Remote API → Backend.

![Architecture Diagram](01_architecture_diagram.svg)

---

## 2. Component Diagram

All major components (Application, Activities, ViewModels, Repositories, API clients,
DI modules, Services) and their dependency relationships.

![Component Diagram](02_component_diagram.svg)

---

## 3. UI Hierarchy / Screen Structure

Full Composable tree from `MainActivity` down to every leaf-level UI component,
split into Auth, User (citizen), and Driver branches.

![UI Hierarchy](03_ui_hierarchy.svg)

---

## 4. Navigation / User Flow

Screen-to-screen transitions, role-based routing (USER vs DRIVER),
auth gating, FCM deep-links, and bottom-nav flows.

![Navigation Flow](04_navigation_flow.svg)

---

## 5. Data Flow / State Management

End-to-end reactive data flow:
UI events → ViewModel (StateFlow) → Repository → API Client → Network → Backend,
plus return path (response → state update → recomposition) and FCM side-channel.

![Data Flow](05_data_flow.svg)

---

## Tech Stack Summary

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (partial MVI for some screens) |
| Navigation | Jetpack Navigation Compose |
| DI | Koin (`appModule`) |
| HTTP (primary) | Retrofit + OkHttp |
| HTTP (secondary) | Ktor HttpClient |
| Push Notifications | Firebase Cloud Messaging (FCM) |
| Analytics | Firebase Analytics |
| Image Loading | Coil (AsyncImage) |
| Persistence | SharedPreferences (`auth_prefs`) |
| State | Kotlin StateFlow + Coroutines |
| Backend | Node.js REST API (Render.com) |
"""

with open(os.path.join(OUT, "README.md"), "w", encoding="utf-8") as f:
    f.write(readme)
print("✓  README.md")
print("\nAll diagrams generated successfully!")
