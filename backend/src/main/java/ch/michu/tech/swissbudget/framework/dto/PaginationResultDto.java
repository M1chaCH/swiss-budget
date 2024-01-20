package ch.michu.tech.swissbudget.framework.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class PaginationResultDto<T> {
    private int pageSize;
    private long totalSize;
    private List<T> pageData;
}
