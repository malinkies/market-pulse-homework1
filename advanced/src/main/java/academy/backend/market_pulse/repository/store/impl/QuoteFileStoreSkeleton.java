package academy.backend.market_pulse.repository.store.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import academy.backend.market_pulse.client.InstrumentQuoteMapper;
import academy.backend.market_pulse.dto.InstrumentQuoteDto;
import academy.backend.market_pulse.model.Quote;
import academy.backend.market_pulse.repository.store.QuoteFileStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, CSV, построчный текст); главное,
 * чтобы {@code load(save(quotes))} возвращал те же котировки. Проверьте себя тестом
 * {@code QuoteFileStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class QuoteFileStoreSkeleton implements QuoteFileStore {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void save(Path path, List<Quote> quotes) {
        try {
            Files.createDirectories(path.toAbsolutePath().getParent());
            List<InstrumentQuoteDto> dtos = quotes.stream().map(InstrumentQuoteMapper::toDto).toList();

            mapper.writeValue(path.toFile(), dtos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Quote> load(Path path) {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(path);
            return InstrumentQuoteMapper.fromJson(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
