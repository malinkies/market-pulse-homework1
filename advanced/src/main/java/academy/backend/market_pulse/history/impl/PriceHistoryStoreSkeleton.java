package academy.backend.market_pulse.history.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import academy.backend.market_pulse.history.PriceHistoryStore;
import academy.backend.market_pulse.history.PricePoint;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (бинарный, текстовый и т.д.); главное,
 * чтобы каждый {@link #append} создавал НОВЫЙ файл (не переписывал предыдущий) и
 * {@link #load(Path, String)} возвращал все сохранённые точки по тикеру в порядке возрастания
 * времени. Проверьте себя тестом {@code PriceHistoryStoreContractTest} (замените в нём эталонную
 * реализацию на свою).
 */
public class PriceHistoryStoreSkeleton implements PriceHistoryStore {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Path append(Path directory, PricePoint point) {
        try {
            Files.createDirectories(directory);
            String filename = point.ticker() + "_" +
                    point.timestamp().toString().replace(":", "-") +
                    ".json";
            Path path = directory.resolve(filename);
            mapper.writeValue(path.toFile(), point);
            return path;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<PricePoint> load(Path directory, String ticker) {
        if (!Files.exists(directory)) {
            return List.of();
        }
        List<PricePoint> result = new ArrayList<>();
        try (Stream<Path> files = Files.list(directory)) {
            List<Path> sortedFiles = files
                    .filter(p -> p.getFileName().toString().startsWith(ticker + "_"))
                    .sorted().toList();

            for (Path path : sortedFiles) {
                result.add(mapper.readValue(path.toFile(), PricePoint.class));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
