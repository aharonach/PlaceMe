import { useEffect } from 'react';
import useAxios from "./useAxios";
import {extractListFromAPI} from "../utils";

const useFetchList = ({ fetchUrl, propertyName, thenCallback, mapCallback }) => {
    const [response, error, loading, axiosFetch] = useAxios();

    const getList = () => {
        axiosFetch({
            method: 'get',
            url: fetchUrl,
        }).then(res => thenCallback && thenCallback(res));
    }

    useEffect(() => {
        getList();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const items = extractListFromAPI(response, propertyName, mapCallback);
    const pagination = response?.page;

    if ( items && pagination ) {
        pagination.items = items.length;
        pagination.startsFrom = pagination?.number * pagination?.size + 1;
    }

    return [
        items,
        error,
        loading,
        axiosFetch,
        getList,
        pagination,
    ];
};

export default useFetchList;