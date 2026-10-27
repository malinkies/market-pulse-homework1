package academy.backend.market_pulse.repository.store.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import academy.backend.market_pulse.repository.store.WatchlistStore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (JSON, построчный текст и т.д.); главное,
 * чтобы {@code load(save(tickers))} возвращал тот же набор тикеров в том же порядке. Проверьте себя
 * тестом {@code WatchlistStoreContractTest} (замените в нём эталонную реализацию на свою).
 */
public class WatchlistStoreSkeleton implements WatchlistStore {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void save(Path path, Set<String> tickers) {
        try {
            Files.createDirectories(path.toAbsolutePath().getParent());
            mapper.writeValue(path.toFile(), new ArrayList<>(tickers));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> load(Path path) {
        if (!Files.exists(path)) {
            return new LinkedHashSet<>();
        }
        try {
            List<String> tickers = mapper.readValue(path.toFile(), new TypeReference<List<String>>() {});
            return new LinkedHashSet<>(tickers);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
