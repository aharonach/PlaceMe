import React, {useEffect, useState} from 'react';
import TableList from "../TableList";
import useAxios from "../../hooks/useAxios";
import Loading from "../Loading";
import Api from "../../api";
import {Alert, Button} from "react-bootstrap";
import { extractListFromAPI } from "../../utils";
import {useParams} from "react-router-dom";

export default function PlacementResultsList({}){

    const [placementResults, error, loading, axiosFetch] = useAxios();
    const [count, setCount] = useState(1);
    const { placementId } = useParams();

    const columns = {
        name: "Name",
        description: "Description",
        createdTime: "Created Time",
        numberOfClasses: "Number Of Classes",
        placementScore: "Score",
        status: "Status",
        selected: "Selected Result"
    };

    const getData = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/placements/${placementId}/results`,
        });
    };

    const generateResult = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'post',
            url: `/placements/${placementId}/results/generate`,
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
            {!loading && !error && placementResults && <TableList linkTo={{field: 'name', basePath: `/placements/${placementId}/results/`}} items={extractListFromAPI(placementResults, 'placementResultList')} columns={columns} />}
        </>
    );
}
