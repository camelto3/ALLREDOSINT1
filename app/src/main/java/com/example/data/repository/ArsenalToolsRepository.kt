package com.example.data.repository

import com.example.data.model.ArsenalTool
import com.example.data.model.ToolCategory

class ArsenalToolsRepository {

    val allTools: List<ArsenalTool> = listOf(
        ArsenalTool(
            id = "sherlock",
            name = "Sherlock",
            category = ToolCategory.IDENTITY_PERSONA,
            tagline = "Hunt down social media accounts across 350+ networks",
            description = "Sherlock is an open-source OSINT powerhouse that takes a target username or alias and probes over 350 major social networks, forums, coding repositories, and blogging platforms simultaneously.",
            howToUse = "1. Enter the target alias into the interactive Sherlock console.\n2. In CLI: run `sherlock <username> --timeout 10 --print-found --csv`.\n3. Analyze positive hits (HTTP 200 OK) to identify active digital footprints, alternate handles, and geographic clues.",
            primarySyntax = "sherlock target_alias --timeout 10 --folderoutput ./recon_data",
            keyFeatures = listOf("350+ Site Manifest", "Tor Routing Support (--tor)", "High-Concurrency Async Probing", "CSV/JSON Export"),
            riskOrDefensiveNote = "To prevent alias harvesting, security analysts recommend enforcing unique, non-reused handles across sensitive accounts.",
            officialUrl = "https://github.com/sherlock-project/sherlock",
            iconType = "person_search"
        ),
        ArsenalTool(
            id = "intelligence_x",
            name = "Intelligence X (IntelX)",
            category = ToolCategory.BREACH_DARKNET,
            tagline = "Darknet, data leak, IP CIDR, and public paste intelligence search engine",
            description = "Intelligence X (intelx.io) indexes deep web and dark web content, historical domain snapshots, paste repositories, public leak dumps, and blockchain records with unfiltered multi-selector search.",
            howToUse = "1. Input selectors such as an email address, domain, Bitcoin wallet, IP CIDR, or hash.\n2. Review historical leak records, date-stamped paste dumps, and leaked database files.\n3. Integrate via Python API using `intelx.search('<selector>')`.",
            primarySyntax = "intelx-cli --search \"target-domain.com\" --limit 50 --dump",
            keyFeatures = listOf("Darknet .onion Indexing", "Raw Historical Pastes", "Selectors by Hash / Email / IP / CIDR", "Historical WHOIS & DNS"),
            riskOrDefensiveNote = "Leverage IntelX alerts to proactively detect compromised organizational credentials before they are weaponized.",
            officialUrl = "https://intelx.io",
            iconType = "search"
        ),
        ArsenalTool(
            id = "pimeyes",
            name = "PimEyes",
            category = ToolCategory.IDENTITY_PERSONA,
            tagline = "AI facial recognition reverse image search engine",
            description = "PimEyes performs advanced biometric facial recognition across billions of indexed web pages, videos, news articles, and blogs to locate matching photos of a subject regardless of background or angle.",
            howToUse = "1. Upload a clear, front-facing portrait of the target subject.\n2. Filter results by date or safe search parameters.\n3. Review matched URLs to discover unknown public profiles, event appearances, or unauthorized photo use.",
            primarySyntax = "Upload Image -> Extract 128D Face Embedding -> Match against Global Image Graph",
            keyFeatures = listOf("Neural Facial Biometrics", "Cross-Angle Detection", "Source URL Attribution", "Exclusion & Privacy Monitoring"),
            riskOrDefensiveNote = "Used defensively to monitor executive biometric exposure and prevent deepfake/impersonation attacks.",
            officialUrl = "https://pimeyes.com",
            iconType = "photo_camera"
        ),
        ArsenalTool(
            id = "social_analyzer",
            name = "Social Analyzer",
            category = ToolCategory.IDENTITY_PERSONA,
            tagline = "API and Web profile investigator across 1,000+ social platforms",
            description = "Social Analyzer is an advanced API, CLI, and Web tool that analyzes profile metadata, bio descriptions, location tags, and cross-platform associations across over 1,000 online websites.",
            howToUse = "1. Pass target profile username, email, or full name.\n2. Run CLI with `social-analyzer --username <target> --logs --top 100`.\n3. Review extracted bio text, linked social links, and visual avatar correlation.",
            primarySyntax = "social-analyzer --username target_handle --metadata --websites all",
            keyFeatures = listOf("1,000+ Platform Modules", "Bio & Metadata Scraping", "Fast REST API Interface", "Pattern Recognition"),
            riskOrDefensiveNote = "Unifies cross-platform profile correlations to detect brand impersonation and synthetic identity creation.",
            officialUrl = "https://github.com/qeeqbox/social-analyzer",
            iconType = "language"
        ),
        ArsenalTool(
            id = "maltego",
            name = "Maltego",
            category = ToolCategory.INFRASTRUCTURE_IOT,
            tagline = "Interactive graph-based link analysis and threat intelligence visualization",
            description = "Maltego visualizes complex relationships between people, companies, domains, DNS records, IP blocks, social media profiles, and autonomous systems using automated transform modules.",
            howToUse = "1. Create a new graph and drag an Entity node (Domain, Person, Netblock, Email).\n2. Right-click the entity and run Transforms (e.g., 'To DNS Name', 'To IP Address', 'To Email Addresses').\n3. Identify high-value infrastructure clusters and pivot deeper into threat actors.",
            primarySyntax = "Run Transforms: Entity(Domain) -> Transform(DNS_To_IP) -> Transform(IP_To_ASN)",
            keyFeatures = listOf("Dynamic Node Graph Visualizer", "Hub Transforms (Shodan, VirusTotal, Censys)", "Export to PDF/GraphML", "Entity Clustering"),
            riskOrDefensiveNote = "Essential for security operations centers (SOC) to map APT adversary infrastructure and command-and-control nodes.",
            officialUrl = "https://www.maltego.com",
            iconType = "hub"
        ),
        ArsenalTool(
            id = "hibp",
            name = "Have I Been Pwned (HIBP)",
            category = ToolCategory.BREACH_DARKNET,
            tagline = "Industry standard data breach & leaked credential repository",
            description = "Created by security researcher Troy Hunt, Have I Been Pwned indexes billions of compromised accounts and passwords from commercial data breaches, allowing instant verification of breach status.",
            howToUse = "1. Query an email or domain to identify breaches it was exposed in.\n2. Query password SHA-1 prefix (k-Anonymity model) using the Pwned Passwords API.\n3. Integrate domain monitoring to receive real-time notifications when company emails appear in new breaches.",
            primarySyntax = "GET https://haveibeenpwned.com/api/v3/breachedaccount/{account}",
            keyFeatures = listOf("13+ Billion Compromised Records", "k-Anonymity Password Model", "Domain Wide Subscriptions", "Detailed Breach Metadata"),
            riskOrDefensiveNote = "Enforce automated HIBP API checks at user registration to prevent the use of known compromised passwords (NIST SP 800-63B compliance).",
            officialUrl = "https://haveibeenpwned.com",
            iconType = "mark_email_unread"
        ),
        ArsenalTool(
            id = "dehashed",
            name = "DeHashed",
            category = ToolCategory.BREACH_DARKNET,
            tagline = "Asset & credential breach search index with hashed password lookup",
            description = "DeHashed is an extensive breach intelligence search engine for cybersecurity professionals, providing cross-search capabilities by email, username, IP, name, phone, VIN, and cryptographic hashes.",
            howToUse = "1. Enter search queries with boolean operators (e.g., `email:target@corp.com` or `domain:corp.com`).\n2. View historical passwords, salt values, and breach origins.\n3. Pivot on identical password hashes or phone numbers to uncover linked accounts.",
            primarySyntax = "curl -u email:api_key 'https://api.dehashed.com/search?query=domain:target.com'",
            keyFeatures = listOf("Wildcard & Boolean Search", "Password Hash Reverse Lookup", "Phone & Address Correlation", "Corporate Asset Auditing"),
            riskOrDefensiveNote = "Assists penetration testers and enterprise defenders in identifying lingering employee password reuse across third-party services.",
            officialUrl = "https://dehashed.com",
            iconType = "password"
        ),
        ArsenalTool(
            id = "shodan",
            name = "Shodan",
            category = ToolCategory.INFRASTRUCTURE_IOT,
            tagline = "Search engine for Internet-connected devices, open ports, and industrial control systems",
            description = "Shodan continuously scans the entire IPv4 and IPv6 address space, indexing banner data, open ports, SSL/TLS certificates, HTTP headers, SCADA/ICS hardware, webcams, and vulnerable daemons.",
            howToUse = "1. Search using specialized filters: `org:\"Target Org\" port:\"443\" country:\"US\"`.\n2. Look for vulnerable services: `vuln:\"CVE-2024-3400\"` or `has_screenshot:true`.\n3. Use CLI: `shodan host <ip_address>` or `shodan search \"product:nginx\"`.",
            primarySyntax = "shodan search --fields ip_str,port,org,hostnames \"org:'Target Corp' port:22,80,443\"",
            keyFeatures = listOf("Global Port & Banner Index", "Industrial ICS/SCADA Filters", "Vulnerability CVE Correlation", "Shodan Monitor (Alerts)"),
            riskOrDefensiveNote = "Defenders must use Shodan to audit their own external perimeter for shadow IT, misconfigured RDP (port 3389), and exposed database ports.",
            officialUrl = "https://www.shodan.io",
            iconType = "router"
        ),
        ArsenalTool(
            id = "phoneinfoga",
            name = "PhoneInfoga",
            category = ToolCategory.IDENTITY_PERSONA,
            tagline = "Advanced international phone number OSINT framework",
            description = "PhoneInfoga scans international telephone numbers to determine country, carrier, line type (VoIP/Mobile/Landline), timezone, and executes customized Google search dorks across footprint archives.",
            howToUse = "1. Input target phone number in standard international format (e.g. +14155552671).\n2. In CLI: run `phoneinfoga scan -n +14155552671`.\n3. Analyze carrier identity and execute generated Truecaller, Sync.me, and social search dorks.",
            primarySyntax = "phoneinfoga scan -n +14155552671 --recon",
            keyFeatures = listOf("International E.164 Parsing", "VoIP vs Cellular Detection", "Automated Dork Generation", "Web GUI & REST Server"),
            riskOrDefensiveNote = "Helps verify whether suspicious incoming SMS/Voice communications originate from virtual disposable burner numbers.",
            officialUrl = "https://github.com/sundowndev/phoneinfoga",
            iconType = "phone_iphone"
        ),
        ArsenalTool(
            id = "epieos",
            name = "Epieos",
            category = ToolCategory.IDENTITY_PERSONA,
            tagline = "Reverse email & phone number OSINT tool without notifying the target",
            description = "Epieos uncovers registered Google accounts (Google Maps reviews, Google Calendar, Google ID, Profile Photos), Skype handles, Gravatar hashes, and social links tied to an email address without triggering target alerts.",
            howToUse = "1. Enter an email address into the Epieos search interface.\n2. Discover the target's Google User ID (Gaia ID), Google Maps review history, and registered account services.\n3. Pivot on user profile photos and reviewer locations to map geographic movements.",
            primarySyntax = "epieos-search --email target_analyst@gmail.com --enrich-google",
            keyFeatures = listOf("Google Gaia ID Discovery", "Google Maps Review Forensics", "No Notification / Stealth Scan", "Gravatar & Skype Linkage"),
            riskOrDefensiveNote = "Security analysts use Epieos to trace spear-phishing sender emails back to personal real-world identities.",
            officialUrl = "https://epieos.com",
            iconType = "account_circle"
        ),
        ArsenalTool(
            id = "evilginx3",
            name = "Evilginx 3",
            category = ToolCategory.WEB_AUDIT_PROXY,
            tagline = "Adversary-in-the-Middle (AiTM) reverse proxy security assessment framework",
            description = "Evilginx 3 is an advanced security assessment framework used by penetration testers to demonstrate how Adversary-in-the-Middle reverse proxies can bypass traditional 2FA (SMS/TOTP) by capturing session cookies.",
            howToUse = "1. In authorized red team lab: configure `phishlets` for target identity providers.\n2. Configure reverse proxy TLS certificates and DNS lure domains.\n3. Defensive Testing: evaluate whether hardware-bound FIDO2/WebAuthn keys successfully neutralize AiTM interception.",
            primarySyntax = "evilginx -p ./phishlets -c ./config.json (Authorized Assessment Mode)",
            keyFeatures = listOf("Dynamic Reverse Proxy Engine", "YAML-based Phishlet Definitions", "Session Cookie Capture Telemetry", "FIDO2 Resistance Testing"),
            riskOrDefensiveNote = "CRITICAL DEFENSE: Enforce FIDO2 / WebAuthn hardware security keys or Microsoft Entra ID Conditional Access with Phishing-Resistant MFA.",
            officialUrl = "https://github.com/kgretzky/evilginx2",
            iconType = "shield"
        ),
        ArsenalTool(
            id = "cloudfox",
            name = "CloudFox",
            category = ToolCategory.CLOUD_AD,
            tagline = "Automated situational awareness and attack surface mapping for AWS, Azure, and GCP",
            description = "Created by Bishop Fox, CloudFox automates situational awareness and attack path discovery across multi-cloud environments (AWS, Azure, Google Cloud), identifying exploitable permissions and exposed assets.",
            howToUse = "1. Authenticate to target cloud using read-only or auditing credentials.\n2. Run `cloudfox aws --profile <name> all-checks`.\n3. Review generated loot tables for overly permissive IAM roles, exposed storage buckets, unencrypted databases, and privilege escalation paths.",
            primarySyntax = "cloudfox aws --profile auditor all-checks --out-dir ./cloud_audit",
            keyFeatures = listOf("Multi-Cloud Support (AWS/Azure/GCP)", "IAM Privilege Escalation Detection", "Exposed Storage Bucket Scanner", "Actionable Markdown Loot Tables"),
            riskOrDefensiveNote = "Implement automated Cloud Security Posture Management (CSPM) and Least Privilege IAM policies to remediate findings.",
            officialUrl = "https://github.com/BishopFox/cloudfox",
            iconType = "cloud"
        ),
        ArsenalTool(
            id = "spiderfoot",
            name = "SpiderFoot",
            category = ToolCategory.BREACH_DARKNET,
            tagline = "Open source automated OSINT collection daemon and attack surface monitoring engine",
            description = "SpiderFoot integrates over 200 OSINT data sources into an automated pipeline that maps IP addresses, domain names, e-mail addresses, ASN numbers, and phone numbers into structured threat intelligence.",
            howToUse = "1. Launch the SpiderFoot web server (`sf.py -l 127.0.0.1:5001`).\n2. Start a new scan with a target seed (e.g. `corp-domain.com`).\n3. Select scan modules: Passive Recon, Attack Surface Mapping, or Dark Web Leaks, and monitor correlation charts.",
            primarySyntax = "python3 sf.py -s target-domain.com -m sfp_dns,sfp_shodan,sfp_whois -o json",
            keyFeatures = listOf("200+ Integrated API Modules", "Automated Entity Correlation", "Visual Relationship Graph", "Continuous Scheduled Scans"),
            riskOrDefensiveNote = "Ideal for continuous external attack surface management (EASM) to identify newly exposed subdomains and services.",
            officialUrl = "https://github.com/smicallef/spiderfoot",
            iconType = "radar"
        ),
        ArsenalTool(
            id = "caido",
            name = "Caido",
            category = ToolCategory.WEB_AUDIT_PROXY,
            tagline = "Lightweight, modern web security auditing proxy and request interceptor",
            description = "Caido is a next-generation web application security proxy written in Rust. It offers extreme speed, a sleek modern UI, request tampering, automated sitemapping, match-and-replace rules, and replay capabilities.",
            howToUse = "1. Start Caido daemon on port 8080 and install the local CA certificate into browser.\n2. Intercept and inspect HTTP/HTTPS requests.\n3. Send requests to the 'Replay' tab to test parameter tampering, authentication bypasses, and IDOR vulnerabilities.",
            primarySyntax = "caido-cli --listen-port 8080 --data-dir ~/.caido",
            keyFeatures = listOf("High-Performance Rust Core", "Automated Request Sitemap", "Advanced Match & Replace", "Multi-Tab Replay & Tamper Engine"),
            riskOrDefensiveNote = "Use Caido to audit web APIs against OWASP Top 10 vulnerabilities (Injection, Broken Object Level Auth, Security Misconfigurations).",
            officialUrl = "https://caido.io",
            iconType = "bug_report"
        ),
        ArsenalTool(
            id = "nuclei",
            name = "Nuclei",
            category = ToolCategory.WEB_AUDIT_PROXY,
            tagline = "Fast, community-powered vulnerability and misconfiguration scanner",
            description = "Nuclei (by ProjectDiscovery) uses declarative YAML templates to scan web servers, APIs, cloud assets, and networks for thousands of CVEs, misconfigurations, default credentials, and zero-day vulnerabilities.",
            howToUse = "1. Update templates: `nuclei -update-templates`.\n2. Scan a target domain or IP list: `nuclei -u https://target.com -severity critical,high`.\n3. Export findings to structured JSON or Markdown reports.",
            primarySyntax = "nuclei -u https://target.com -tags cve,auth-bypass -severity critical,high -o report.txt",
            keyFeatures = listOf("7,000+ Community YAML Templates", "Massively Parallel Execution", "Custom Template Creation Engine", "CI/CD Pipeline Integration"),
            riskOrDefensiveNote = "Integrate Nuclei in continuous DevSecOps pipelines to block known vulnerabilities before production deployment.",
            officialUrl = "https://github.com/projectdiscovery/nuclei",
            iconType = "flash_on"
        ),
        ArsenalTool(
            id = "cyberchef",
            name = "CyberChef",
            category = ToolCategory.DATA_FORENSICS,
            tagline = "The Cyber Swiss Army Knife for encoding, decoding, hashing, and forensic parsing",
            description = "Created by GCHQ, CyberChef is an essential web-based and client-side utility for encoding, decoding, encrypting, decrypting, hashing, compression, deobfuscating malware payloads, and parsing timestamps.",
            howToUse = "1. Paste encoded or obfuscated text into the Input pane.\n2. Select operations (e.g., 'From Base64', 'ROT13', 'Defang URL', 'Extract IP addresses').\n3. Chain operations sequentially into a 'Recipe' to peel back layers of obfuscation.",
            primarySyntax = "Recipe: From_Base64('A-Z0-9+/=') -> Gunzip() -> Extract_URLs()",
            keyFeatures = listOf("300+ Forensic Operations", "Interactive Recipe Chaining", "Hex, Base64, URL, JWT, Hashing", "Defang / Refang IOC Tools"),
            riskOrDefensiveNote = "Invaluable for SOC analysts deobfuscating suspicious PowerShell, VBScript, and base64 encoded command lines.",
            officialUrl = "https://gchq.github.io/CyberChef",
            iconType = "build"
        ),
        ArsenalTool(
            id = "bloodhound",
            name = "BloodHound",
            category = ToolCategory.CLOUD_AD,
            tagline = "Active Directory and Azure graph-based attack path and privilege escalation mapper",
            description = "BloodHound uses graph theory to reveal hidden, unintended relationships and access paths within an Active Directory or Microsoft Entra ID (Azure) environment (e.g. Domain Admin compromise paths).",
            howToUse = "1. In authorized audit: collect domain data using SharpHound (`SharpHound.exe -c All`).\n2. Import collected ZIP file into BloodHound GUI.\n3. Run pre-built queries: 'Shortest Paths to Domain Admins', 'Find Principals with DCSync Rights', or 'Unconstrained Delegation'.",
            primarySyntax = "SharpHound.exe -c All --domain target.local --zipfilename ad_recon.zip",
            keyFeatures = listOf("Neo4j Graph Database", "Pre-built Attack Path Queries", "Azure AD / Entra ID Support", "DCSync & ACL Abuse Identification"),
            riskOrDefensiveNote = "Enterprise administrators use BloodHound defensively to eliminate Tier-0 attack paths and audit Kerberoasting exposure.",
            officialUrl = "https://github.com/BloodHoundAD/BloodHound",
            iconType = "account_tree"
        ),
        ArsenalTool(
            id = "recon_ng",
            name = "Recon-ng",
            category = ToolCategory.INFRASTRUCTURE_IOT,
            tagline = "Full-featured reconnaissance framework with modular database management",
            description = "Recon-ng is a powerful, Metasploit-like open source intelligence framework with an interactive command-line interface, modular architecture, database storage for gathered records, and automated reporting.",
            howToUse = "1. Launch `recon-ng` and create a workspace: `workspaces create target_recon`.\n2. Add domain: `db insert domains target.com`.\n3. Load recon modules: `modules load recon/domains-hosts/brute_hosts` and `run`.",
            primarySyntax = "recon-ng -w corp_recon -r ./scripts/auto_recon.rc",
            keyFeatures = listOf("Command-line Modular Interface", "Built-in SQLite Records Database", "Dozens of Recon Modules", "HTML / CSV Reporting"),
            riskOrDefensiveNote = "Keeps OSINT investigation findings organized in structured database tables for complex threat attribution.",
            officialUrl = "https://github.com/lanmaster53/recon-ng",
            iconType = "terminal"
        )
    )

    fun getToolById(id: String): ArsenalTool? = allTools.find { it.id == id }
    fun getToolsByCategory(category: ToolCategory): List<ArsenalTool> = allTools.filter { it.category == category }
}
