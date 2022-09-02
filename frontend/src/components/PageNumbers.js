import {Pagination, Stack} from "react-bootstrap";

export default function PageNumbers({ pagination, setPage, arrows }) {
    if ( ! pagination ) {
        return;
    }

    let active = pagination.number + 1;
    let items = [];

    const showing = pagination.size >= pagination.totalElements ? pagination.totalElements : pagination.size;

    for (let number = 1; number <= pagination.totalPages; number++) {
        items.push(
            <Pagination.Item key={number} active={number === active} onClick={() => setPage(number)}>
                {number}
            </Pagination.Item>,
        );
    }

    return (
        <Stack direction="horizontal" className="align-items-baseline" gap={2}>
            <Pagination>
                {arrows && <Pagination.Prev onClick={() => setPage(active - 1)} disabled={active <= 1} />}
                {items}
                {arrows && <Pagination.Next onClick={() => setPage(active + 1)} disabled={active >= pagination.totalPages} />}
            </Pagination>
            <small className="text-muted">
                Showing {showing} out of {pagination.totalElements}
            </small>
        </Stack>
    );
}