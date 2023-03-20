import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px

pio.kaleido.scope.mathjax = None


fig = go.Figure()

fig.add_trace(go.Scatter(name="DesisCen", x=[1, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[2673205.93, 876913.8, 362475.64, 223458.038, 154271.15, 129632.158]
                         , line=dict(color='rgb(99,110,250)', width=2), marker=dict(size=5, symbol='circle')))
fig.add_trace(go.Scatter(name="Disco", x=[1, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[2181646, 2124146.77, 2071365.54, 2032837.087, 2058517.16, 2092535.16]
                         , line=dict(color='rgb(239,85,59)', width=2), marker=dict(size=5, symbol='square')))
fig.add_trace(go.Scatter(name="Scotty", x=[1, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[9289379.03, 9294990.7, 9154596.34, 9221778.22, 9226710, 9189027.43]
                         , line=dict(color='rgb(0,204,150)', width=2), marker=dict(size=5, symbol='diamond')))
fig.add_trace(go.Scatter(name="Desis", x=[1, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[30545075.4,	30170030.66, 29975328.87, 29993363.04, 30042764.4, 30084216.82]
                         , line=dict(color='rgb(171,99,250)', width=2), marker=dict(size=5, symbol='cross')))

# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))



#legend
fig.update_layout(
    legend=dict(
        yanchor="top",
        y=0.99,
        xanchor="left",
        x=0.01,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=10,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="events/sec",
        titlefont=dict(size=15),
        ticktext=["0", "2M", "5M", "10M", "20M", "30M"],
        tickvals=[0, 2000000, 5000000, 10000000, 20000000, 30000000],
        range=[0,32000000],
        tickmode="array",
    ),
    xaxis=dict(
        title_text="queries",
        titlefont=dict(size=15),
        ticktext=["1", "10", '100', "1000"],
        tickvals=[1, 10, 100, 1000],
        range=[0,3],
        type="log"
    )
)

# size & color
fig.update_layout(
    autosize=False,
    width=500,
    height=500,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)'
)

# fig.update_yaxes(automargin=True)
# fig.update_yaxes(ticks="outside", tickwidth=1, tickcolor='black', ticklen=5)
fig.update_xaxes(showline=True, linewidth=1, linecolor='black', mirror=True)
fig.update_yaxes(showline=True, linewidth=1, linecolor='black', mirror=True)

fig.show()
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")

# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/throughput\ThroughputMulA.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/throughput\ThroughputMulA.svg")