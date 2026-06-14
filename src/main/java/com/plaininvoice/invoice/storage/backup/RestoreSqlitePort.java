package com.plaininvoice.invoice.storage.backup;

import java.nio.file.*;

interface RestoreSqlitePort {

    String integrity(Path copy) throws Exception;
}
