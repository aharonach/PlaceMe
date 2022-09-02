import React, {useEffect, useState} from 'react';
import useFetchList from "../hooks/useFetchList";
import {LinkContainer} from "react-router-bootstrap";
import {Alert, Button, ButtonGroup} from "react-bootstrap";
import TableList from "./TableList";
import PageNumbers from "./PageNumbers";

const buildUrl = (url, params) => {
    const searchParams = new URLSearchParams();

    Object.keys(params).forEach(key => {
        if ( params[key] === undefined || params[key] === null ) {
            return;
        }

        searchParams.append(key, params[key]);
    });

    const queryString = searchParams.toString();
    return url + (queryString ? '?' + queryString : '');
}

export default function RecordList({
       fetchUrl = '',
       propertyName = '',
       addButton,
       title,
       columns = {},
       basePath = '',
       linkField = '',
       updated,
       additionalButtons,
       mapCallback,
       showPagination = true,
       sorting,
       totals,
       children
    }) {
    const [page, setPage] = useState(1);
    const [sort, setSort] = useState(null);
    const [direction, setDirection] = useState('ASC');
    const [list, error, loading, axiosFetch, getList, pagination] = useFetchList({
        fetchUrl: buildUrl(fetchUrl, { page: page - 1, sortField: sort, sortDirection: direction }),
        propertyName: propertyName,
        mapCallback: mapCallback,
    });

    useEffect(() => {
        getList();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [updated, page, sort, direction]);

    const addButtonRender = addButton && <LinkContainer to="add"><Button>{addButton}</Button></LinkContainer>;
    const pageNumbers = showPagination ? <PageNumbers pagination={pagination} setPage={setPage} arrows totals /> : null;

    return (
        <>
            {title}
            <ButtonGroup className="mb-3">
                {addButtonRender}
                {additionalButtons}
            </ButtonGroup>
            {pageNumbers}
            {error && <Alert variant="danger">{error}</Alert>}
            <>
                {children}
                <TableList
                    linkTo={{field: linkField, basePath: basePath ?? fetchUrl}}
                    items={list}
                    numbering={{enabled: true, startFrom: pagination?.number * pagination?.size + 1}}
                    columns={columns}
                    sorting={ sorting ? {value: sort, set: setSort, fields: sorting } : null}
                    direction={ sorting ? {value: direction, set: setDirection} : null}
                />
            </>
            {pageNumbers}
        </>
    )
}