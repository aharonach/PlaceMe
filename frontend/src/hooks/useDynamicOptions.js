import {useEffect, useState} from "react";
import Api from "../api";
import {extractListFromAPI, prepareCheckboxGroup} from "../utils";

const useDynamicOptions = (fetchUrl, property, placeholder, mapCallback) => {
    const map = mapCallback || prepareCheckboxGroup('id', 'name' );
    const [data, setData] = useState([]);

    const getData = () => {
        Api.get(fetchUrl).then(res => {
            const options = extractListFromAPI(res.data, property, map);
            options.unshift({ value: '', label: 'Select...'});
            setData(options);
        });
    }

    useEffect(() => {
        getData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return data ? data : [{ value: '', label: 'Loading...' }];
}

export default useDynamicOptions;