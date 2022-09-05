import React, {useEffect, useState} from 'react';
import useFetchList from "../hooks/useFetchList";
import {LinkContainer} from "react-router-bootstrap";
import {Alert, Button, ButtonGroup, Stack} from "react-bootstrap";
import TableList from "./TableList";
import PageNumbers from "./PageNumbers";
import HeroAddRecord from "./General/HeroAddRecord";
import Loading from "./Loading";

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
       hero,
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
    const resetSort = () => {
        setSort(null);
        setDirection('ASC');
    };

    useEffect(() => {
        getList();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [page, sort, direction]);

    const addButtonRender = addButton && <LinkContainer to="add"><Button>{addButton}</Button></LinkContainer>;
    const resetSortingButton = sort && <Button size={"sm"} variant="link" onClick={resetSort} className="mb-3 p-0">Reset sorting</Button>;
    const pageNumbers = showPagination ? <PageNumbers pagination={pagination} setPage={setPage} arrows totals /> : null;
    const topBottomRow = <Stack gap={2} direction="horizontal">{pageNumbers}{resetSortingButton}</Stack>;

    return (
        <>
            {title}
            <ButtonGroup className="mb-3">
                {list?.length > 0 && addButtonRender}
                {additionalButtons}
            </ButtonGroup>
            {error && <Alert variant="danger">{error}</Alert>}
            {children}
            {!list ? <Loading show={loading} />
                : list.length <= 0
                    ? (hero ? hero : <HeroAddRecord />)
                    : <>
                        {topBottomRow}
                        <TableList
                            linkTo={{field: linkField, basePath: basePath ?? fetchUrl}}
                            items={list}
                            numbering={{enabled: true, startFrom: pagination?.startsFrom}}
                            columns={columns}
                            sorting={ sorting ? {value: sort, set: setSort, fields: sorting} : null}
                            direction={ sorting ? {value: direction, set: setDirection} : null}
                        />
                        {topBottomRow}
                    </>
            }
        </>
    )
}

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
