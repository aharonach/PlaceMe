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

    const pagination = response?.page;

    return [
        extractListFromAPI(response, propertyName, mapCallback),
        error,
        loading,
        axiosFetch,
        getList,
        pagination,
    ];
};

export default useFetchList;