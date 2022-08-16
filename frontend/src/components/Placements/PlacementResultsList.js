import React, {useEffect, useState} from 'react';
import TableList from "../TableList";
import useAxios from "../../hooks/useAxios";
import Loading from "../Loading";
import Api from "../../api";
import {Alert, Button} from "react-bootstrap";
import { extractListFromAPI } from "../../utils";

export default function PlacementResultsList({placement}){

    const [placementResults, error, loading, axiosFetch] = useAxios();
    const [count, setCount] = useState(1);

    const columns = {
        name: "Name",
        description: "Description",
        createdTime: "Created Time",
    };

    const getData = () => {
        console.log(placement);

        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/placements/${placement.id}/results`,
        });
    };

    const generateResult = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'post',
            url: `/placements/${placement.id}/results/generate`,
        }).then(() => {
            setCount(count + 1);
        });
    }

    useEffect(() => {
        getData();
    }, [count]);

    return (
        <>
            <Button variant="primary" onClick={generateResult}>Generate Result</Button>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && placementResults && <TableList linkTo={{field: 'name', basePath: '/placements/'}} items={extractListFromAPI(placementResults, 'placementResultList')} columns={columns} />}
        </>
    );
}
