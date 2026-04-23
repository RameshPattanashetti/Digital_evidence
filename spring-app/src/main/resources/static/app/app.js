const API_BASE = resolveApiBase();

let flash = null;
let lastSearchedEvidenceId = null;

function showFlash(message, isError = false) {
    if (!flash) {
        return;
    }

    flash.hidden = false;
    flash.textContent = isError ? friendlyErrorMessage(message) : message;
    flash.classList.toggle('error', isError);
}

function normalizeError(err, fallback) {
    if (typeof err === 'string' && err.trim()) {
        return friendlyErrorMessage(err);
    }
    if (err && typeof err.message === 'string' && err.message.trim()) {
        return friendlyErrorMessage(err.message);
    }
    return friendlyErrorMessage(fallback);
}

function friendlyErrorMessage(message) {
    const text = typeof message === 'string' ? message.trim() : '';
    if (!text) {
        return 'Something went wrong. Please try again.';
    }

    const lower = text.toLowerCase();
    if (lower.includes('not found')) {
        return 'We could not find that item. Please check the selected value and try again.';
    }
    if (lower.includes('not reachable') || lower.includes('connection') || lower.includes('failed to fetch')) {
        return 'The server is not responding right now. Start the app and try again.';
    }
    if (lower.includes('request failed')) {
        return 'We could not complete that request right now. Please try again.';
    }
    if (lower.includes('unable to load')) {
        return text;
    }

    return text;
}

function resolveApiBase() {
    const urlParam = new URLSearchParams(window.location.search).get('apiBase');
    if (urlParam && urlParam.trim()) {
        return `${urlParam.replace(/\/$/, '')}/api/mongo/v1`;
    }

    const saved = localStorage.getItem('de-api-base');
    if (saved && saved.trim()) {
        return `${saved.replace(/\/$/, '')}/api/mongo/v1`;
    }

    const origin = window.location.origin;
    if (origin.includes(':8081') || origin.includes(':8082')) {
        return '/api/mongo/v1';
    }

    // Fallback for Live Server/file-hosted frontend.
    return 'http://localhost:8082/api/mongo/v1';
}

async function apiRequest(path, options = {}) {
    const response = await fetch(`${API_BASE}${path}`, {
        headers: {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        },
        ...options
    });

    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    let body = null;
    if (text) {
        try {
            body = JSON.parse(text);
        } catch {
            body = { message: text };
        }
    }
    if (!response.ok) {
        const message = body && (body.message || body.error || body.path)
            ? (body.message || body.error || `Request failed at ${body.path}`)
            : `Request failed with status ${response.status}`;
        throw new Error(message);
    }

    return body;
}

async function listCases() {
    return apiRequest('/cases');
}

async function listEvidence() {
    return apiRequest('/evidence');
}

async function createCase(payload) {
    return apiRequest('/cases', {
        method: 'POST',
        body: JSON.stringify(payload)
    });
}

async function addEvidence(payload) {
    return apiRequest('/evidence', {
        method: 'POST',
        body: JSON.stringify(payload)
    });
}

async function transferCustody(payload) {
    return apiRequest('/custody', {
        method: 'POST',
        body: JSON.stringify(payload)
    });
}

async function searchEvidence(evidenceId) {
    return apiRequest(`/evidence/${encodeURIComponent(evidenceId)}`);
}

async function deleteEvidence(evidenceId) {
    return apiRequest(`/evidence/${encodeURIComponent(evidenceId)}`, {
        method: 'DELETE'
    });
}

async function getCase(caseId) {
    return apiRequest(`/cases/${encodeURIComponent(caseId)}`);
}

async function getCustodyHistory(evidenceId) {
    return apiRequest(`/custody/evidence/${encodeURIComponent(evidenceId)}`);
}

function toIsoDateTime(datetimeLocal) {
    if (!datetimeLocal) {
        return new Date().toISOString();
    }

    const parsed = new Date(datetimeLocal);
    if (Number.isNaN(parsed.getTime())) {
        return new Date().toISOString();
    }

    return parsed.toISOString();
}

function setActiveNav() {
    const pageName = window.location.pathname.split('/').pop() || 'index.html';
    document.querySelectorAll('.rail-nav a').forEach((link) => {
        const href = link.getAttribute('href');
        link.classList.toggle('active', href === pageName);
    });
}

async function renderCaseButtons(container, onClick) {
    const data = await listCases();
    container.innerHTML = '';

    if (!data.length) {
        return null;
    }

    data.forEach((item) => {
        const btn = document.createElement('button');
        btn.type = 'button';
        const label = item.caseNumber || item.id;
        btn.textContent = `${label} - ${item.title}`;
        btn.addEventListener('click', () => onClick(item.id));
        container.appendChild(btn);
    });

    return data[0].id;
}

function bindCreateCasePage() {
    const form = document.getElementById('createCaseForm');
    if (!form) {
        return;
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const payload = Object.fromEntries(new FormData(form).entries());
        try {
            const data = await createCase(payload);
            const label = data.caseNumber || data.id;
            showFlash(`Case created: ${label}`);
            form.reset();
        } catch (err) {
            showFlash(err.message, true);
        }
    });
}

function bindAddEvidencePage() {
    const form = document.getElementById('addEvidenceForm');
    if (!form) {
        return;
    }

    const caseSelect = document.getElementById('caseIdSelect');

    const loadCaseOptions = async () => {
        if (!caseSelect) {
            return;
        }

        caseSelect.innerHTML = '';
        try {
            const data = await listCases();
            if (!data.length) {
                const emptyOption = document.createElement('option');
                emptyOption.value = '';
                emptyOption.textContent = 'No cases found. Create a case first.';
                caseSelect.appendChild(emptyOption);
                return;
            }

            data.forEach((item) => {
                const option = document.createElement('option');
                option.value = item.id;
                const label = item.caseNumber || item.id;
                option.textContent = `${label} - ${item.title}`;
                caseSelect.appendChild(option);
            });
        } catch (err) {
            const errOption = document.createElement('option');
            errOption.value = '';
            errOption.textContent = 'Unable to load cases';
            caseSelect.appendChild(errOption);
            showFlash(normalizeError(err, 'Unable to load cases for evidence form'), true);
        }
    };

    loadCaseOptions();

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const raw = Object.fromEntries(new FormData(form).entries());
        const payload = {
            caseId: raw.caseId,
            type: raw.type,
            description: raw.description,
            date: toIsoDateTime(raw.date),
            location: raw.location
        };
        try {
            const data = await addEvidence(payload);
            showFlash(`Evidence added: ${data.id}`);
            form.reset();
            await loadCaseOptions();
        } catch (err) {
            showFlash(err.message, true);
        }
    });

    const refreshCasesBtn = document.getElementById('refreshCaseOptionsBtn');
    if (refreshCasesBtn) {
        refreshCasesBtn.addEventListener('click', async () => {
            await loadCaseOptions();
            showFlash('Case options refreshed');
        });
    }
}

function bindCustodyPage() {
    const form = document.getElementById('transferForm');
    if (!form) {
        return;
    }

    const evidenceSelect = document.getElementById('evidenceIdSelect');

    const loadEvidenceOptions = async () => {
        if (!evidenceSelect) {
            return;
        }

        evidenceSelect.innerHTML = '';
        try {
            const data = await listEvidence();
            if (!data.length) {
                const emptyOption = document.createElement('option');
                emptyOption.value = '';
                emptyOption.textContent = 'No evidence found. Add evidence first.';
                evidenceSelect.appendChild(emptyOption);
                return;
            }

            data.forEach((item) => {
                const option = document.createElement('option');
                option.value = item.id;
                option.textContent = `${item.id} - ${item.description}`;
                evidenceSelect.appendChild(option);
            });
        } catch (err) {
            const errOption = document.createElement('option');
            errOption.value = '';
            errOption.textContent = 'Unable to load evidence';
            evidenceSelect.appendChild(errOption);
            showFlash(normalizeError(err, 'Unable to load evidence for custody transfer'), true);
        }
    };

    loadEvidenceOptions();

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const raw = Object.fromEntries(new FormData(form).entries());
        const payload = {
            evidenceId: raw.evidenceId,
            fromPerson: raw.fromPerson,
            toPerson: raw.toPerson,
            remarks: raw.remarks,
            timestamp: raw.timestamp ? toIsoDateTime(raw.timestamp) : null
        };
        try {
            await transferCustody(payload);
            showFlash('Custody record added');
            form.reset();
            await loadEvidenceOptions();
        } catch (err) {
            showFlash(err.message, true);
        }
    });

    const refreshEvidenceBtn = document.getElementById('refreshEvidenceOptionsBtn');
    if (refreshEvidenceBtn) {
        refreshEvidenceBtn.addEventListener('click', async () => {
            await loadEvidenceOptions();
            showFlash('Evidence options refreshed');
        });
    }
}

function bindSearchPage() {
    const searchForm = document.getElementById('searchForm');
    const searchInput = document.getElementById('searchEvidenceId');
    const searchResult = document.getElementById('searchResult');
    const archiveBtn = document.getElementById('archiveBtn');
    if (!searchForm || !searchInput || !searchResult || !archiveBtn) {
        return;
    }

    searchForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const evidenceId = searchInput.value.trim();
        try {
            const data = await searchEvidence(evidenceId);
            const history = await getCustodyHistory(evidenceId);
            lastSearchedEvidenceId = evidenceId;
            searchResult.textContent = [
                `Evidence ID: ${data.id}`,
                `Case ID: ${data.caseId}`,
                `Description: ${data.description}`,
                `Type: ${data.type}`,
                `Date: ${data.date}`,
                `Location: ${data.location}`,
                '',
                'Custody History:',
                ...history.map((event) =>
                    `- ${event.timestamp}: ${event.fromPerson} -> ${event.toPerson} (${event.remarks})`
                )
            ].join('\n');
            showFlash('Evidence loaded');
        } catch (err) {
            searchResult.textContent = 'No evidence selected.';
            lastSearchedEvidenceId = null;
            showFlash(err.message, true);
        }
    });

    archiveBtn.addEventListener('click', async () => {
        if (!lastSearchedEvidenceId) {
            showFlash('Search evidence first before delete', true);
            return;
        }
        try {
            await deleteEvidence(lastSearchedEvidenceId);
            showFlash(`Evidence ${lastSearchedEvidenceId} deleted`);
            lastSearchedEvidenceId = null;
            searchResult.textContent = 'No evidence selected.';
        } catch (err) {
            showFlash(err.message, true);
        }
    });
}

function bindReportsPage() {
    const casesContainer = document.getElementById('cases');
    const reportBox = document.getElementById('reportBox');
    const refreshBtn = document.getElementById('refreshCasesBtn');
    if (!casesContainer || !reportBox) {
        return;
    }

    const buildCaseReport = async (caseId) => {
        const selectedCase = await getCase(caseId);
        const allEvidence = await listEvidence();
        const evidenceItems = allEvidence.filter((item) => item.caseId === caseId);

        const lines = [
            `Case Number: ${selectedCase.caseNumber || 'N/A'}`,
            `Case ID: ${selectedCase.id}`,
            `Case Title: ${selectedCase.title}`,
            `Description: ${selectedCase.description}`,
            `Status: ${selectedCase.status}`,
            `Created: ${selectedCase.createdDate}`,
            '',
            `Evidence Count: ${evidenceItems.length}`,
            '----------------------------------------'
        ];

        for (let index = 0; index < evidenceItems.length; index += 1) {
            const item = evidenceItems[index];
            const history = await getCustodyHistory(item.id);
            lines.push(`${index + 1}. Evidence ${item.id}`);
            lines.push(`   Description: ${item.description}`);
            lines.push(`   Type: ${item.type}`);
            lines.push(`   Date: ${item.date}`);
            lines.push(`   Location: ${item.location}`);
            lines.push('   Custody History:');

            if (!history.length) {
                lines.push('     - No custody records');
            } else {
                history.forEach((event) => {
                    lines.push(`     - ${event.timestamp}: ${event.fromPerson} -> ${event.toPerson} (${event.remarks})`);
                });
            }
            lines.push('');
        }

        return lines.join('\n');
    };

    const refresh = async () => {
        const selected = await renderCaseButtons(casesContainer, async (caseId) => {
            reportBox.textContent = 'Loading report...';
            reportBox.textContent = await buildCaseReport(caseId);
        });
        if (!selected) {
            reportBox.textContent = 'No cases found.';
            return;
        }
        reportBox.textContent = 'Loading report...';
        reportBox.textContent = await buildCaseReport(selected);
    };

    refresh().catch((err) => showFlash(normalizeError(err, 'Failed to load reports'), true));

    if (refreshBtn) {
        refreshBtn.addEventListener('click', async () => {
            try {
                await refresh();
                showFlash('Case list refreshed');
            } catch (err) {
                showFlash(normalizeError(err, 'Failed to refresh cases'), true);
            }
        });
    }
}

function bindHomePage() {
    const summary = document.getElementById('dashboardSummary');
    if (!summary) {
        return;
    }

    (async () => {
        try {
            const cases = await listCases();
            const evidence = await listEvidence();
            summary.textContent = [
                `Total Cases: ${cases.length}`,
                `Total Evidence: ${evidence.length}`,
                `API Base: ${API_BASE}`,
                '',
                'Use the navigation panel to open dedicated pages for each workflow.',
                'All data shown here is now loaded from Mongo-backed APIs.'
            ].join('\n');
        } catch (err) {
            summary.textContent = 'Unable to load backend summary.';
            showFlash(normalizeError(err, 'Backend is not reachable. Start Spring app in mongo profile.'), true);
        }
    })();
}

(function init() {
    flash = document.getElementById('flash');
    setActiveNav();
    bindHomePage();
    bindCreateCasePage();
    bindAddEvidencePage();
    bindCustodyPage();
    bindSearchPage();
    bindReportsPage();
})();
