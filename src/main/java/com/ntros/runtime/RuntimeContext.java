package com.ntros.runtime;

import com.ntros.fetcher.ApiSettings;

public record RuntimeContext(ControlToken token, ApiSettings apiSettings) {}
