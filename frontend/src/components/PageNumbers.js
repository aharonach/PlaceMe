// noinspection JSUnresolvedVariable

import {Pagination, Stack} from "react-bootstrap";

export default function PageNumbers({ pagination, setPage, arrows }) {
    if ( ! pagination ) {
        return;
    }

    const totalElements = pagination.totalElements;
    const totalPages = pagination.totalPages;
    const totalItems = pagination.items;
    const startsFrom = pagination.startsFrom;
    const activeNumber = pagination.number + 1;
    const items = [];

    for (let number = 1; number <= totalPages; number++) {
        items.push(
            <Pagination.Item key={number} active={number === activeNumber} onClick={() => setPage(number)}>
                {number}
            </Pagination.Item>,
        );
    }

    return (
        <Stack direction="horizontal" gap={2}>
            {totalPages > 1 && <Pagination>
                {arrows && <Pagination.Prev onClick={() => setPage(activeNumber - 1)} disabled={activeNumber <= 1}/>}
                {items}
                {arrows && <Pagination.Next onClick={() => setPage(activeNumber + 1)} disabled={activeNumber >= totalPages}/>}
            </Pagination>}
            <small className="text-muted mb-3">
                Displaying {startsFrom} to {totalItems + startsFrom - 1} (out of {totalElements})
            </small>
        </Stack>
    );
}