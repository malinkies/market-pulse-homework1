package academy.backend.market_pulse.history.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import academy.backend.market_pulse.history.PriceHistoryStore;
import academy.backend.market_pulse.history.PricePoint;

/**
 * Заготовка для самостоятельной работы (см. «Требования к файловому хранилищу.md», семинар 7) —
 * структура есть, решения нет. Формат файла — на ваш выбор (бинарный, текстовый и т.д.); главное,
 * чтобы каждый {@link #append} создавал НОВЫЙ файл (не переписывал предыдущий) и
 * {@link #load(Path, String)} возвращал все сохранённые точки по тикеру в порядке возрастания
 * времени. Проверьте себя тестом {@code PriceHistoryStoreContractTest} (замените в нём эталонную
 * реализацию на свою).
 */
public class PriceHistoryStoreSkeleton implements PriceHistoryStore {

    @Override
    public Path append(Path directory, PricePoint point) {
        try {
            Files.createDirectories(directory);
            String filename = point.ticker() + "_" +
                    point.timestamp().toString().replace(":", "-") +
                    ".txt";
            Path path = directory.resolve(filename);
            String data = point.ticker() + ";" + point.timestamp() + ";" + point.price();
            Files.writeString(path, data, StandardOpenOption.CREATE_NEW);
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
                    .filter(p -> p.getFileName().toString().startsWith(ticker + "_")).toList();

            for (Path path : sortedFiles) {
                PricePoint point = parse(Files.readString(path));
                if (point.ticker().equals(ticker)) {
                    result.add(point);
                }
            }
            result.sort(Comparator.comparing(PricePoint::timestamp));
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PricePoint parse(String data) {
        String[] params = data.split(";");
        Instant timestamp = Instant.parse(params[1]);
        BigDecimal price = new BigDecimal(params[2]);
        return new PricePoint(params[0], timestamp, price);
    }
}
