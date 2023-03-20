import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px

pio.kaleido.scope.mathjax = None

windowType = ['Median', 'Quantile', 'Average', 'Sum', 'Count', 'Max', 'Min',
              'Tumbling', 'Sliding', 'Punctuation', 'Session', 'Countbased']
widthSin = 0.2
widthAll=[widthSin,widthSin,widthSin,widthSin,widthSin,widthSin,
            widthSin,widthSin,widthSin,widthSin,widthSin]

fig = go.Figure()

fig.add_trace(go.Bar(name="Central", x=windowType, y=[1520597.885, 1557800.284, 3495378.13, 3359127.763, 3341792.98, 3392249.027, 3355436.95,
                    3372245.47, 1966341.8, 2238047.26, 2041280.4, 2181275.89]
                     , legendrank=1, width=widthAll
                     , marker_color='rgb(153, 0, 50)'))
fig.add_trace(go.Bar(name="DesisIC", x=windowType, y=[7231345.29, 7230599.76, 32348346.87, 31675630.53, 32917038.1, 33999379.84, 33600584.11,
                    31256010.27, 31687829.55, 30392116.51, 30565557.23, 15967132.95]
                     , legendrank=2, width=widthAll
                     , marker_color='rgb(49, 0, 125)'))
fig.add_trace(go.Bar(name="DesisSW", x=windowType, y=[7124119.39, 7298107.12, 31171516.62, 31651978.27, 32811652.63, 33526088.52, 33681575.7,
                    31572287.21, 31566440.19, 30735626.13, 30871344.42, 16001392.57]
                     , legendrank=3, width=widthAll
                     , marker_color='rgb(25, 86, 2)'))
fig.add_trace(go.Bar(name="Desis", x=windowType, y=[7401292, 7345260.74, 32429281.64, 33303603.04, 33626397.4, 34126237.66, 34166162.26,
                    32743863.48, 31710032.11, 30776051.23, 30929431.42, 16012225.69]
                     , legendrank=4, width=widthAll
                     , marker_color='rgb(60,60,60)'))
# fig.add_trace(go.Bar(name="DesisSw", x=[" "], y=[30545075.4], legendrank=4, width=[0.18]
#                      , marker_line_color='rgb(255,161,90)', marker_pattern_shape="-"))
# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6)



#legend
fig.update_layout(
    legend=dict(
        orientation="h",
        yanchor="top",
        y=1.2,
        xanchor="left",
        x=0.3,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=30,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="events/sec",
        titlefont=dict(size=35),
        ticktext=["0", "5M", "10M", "15M", "20M", "25M", "30M", "35M"],
        tickvals=[0, 5000000, 10000000, 15000000, 20000000, 25000000, 30000000, 35000000],
        tickmode="array",
        range=[0, 36000000],
        ticks="inside",
        ticklen=20,
        tickwidth=5,
    ),
    xaxis=dict(
        # title_text="local nodes",
        # titlefont=dict(size=15),
        ticktext=['Median', 'Quantile', 'Average', 'Sum', 'Count', 'Max', 'Min',
              'Tumbling', 'Sliding', 'Punctuation', 'Session', 'Countbased'],
        # tickvals=[1, 2, 3, 4],
        tickmode="array",
        # range=[0, 6],
    ),
)

# size & color
fig.update_layout(
    autosize=False,
    width=1200,
    height=600,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)',
    margin=dict(
        l=5,
        r=5,
        b=5,
        t=5,
        pad=0
    ),
)
fig.update_layout(barmode='group', bargap=0.15, bargroupgap=0.0)

fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))
fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')

fig.show()
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\single\singleFunctions.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\single\singleFunctions.svg")